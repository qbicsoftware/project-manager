package life.qbic.connection.openbis;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.vocabulary.VocabularyTerm;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.vocabulary.fetchoptions.VocabularyTermFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.vocabulary.search.VocabularyTermSearchCriteria;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import com.vaadin.data.util.BeanItemContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import life.qbic.beans.ProjectBean;
import life.qbic.beans.ProjectToProjectBeanConverter;


/**
 * Created by sven1103 on 8/12/16.
 */
public class OpenBisConnection {

  private BeanItemContainer<ProjectBean> projectBeanBeanItemContainer = new BeanItemContainer<ProjectBean>(
      ProjectBean.class);
  private String sessionToken;
  private IApplicationServerApi app;
  private IDataStoreServerApi dss;

  public OpenBisConnection(IApplicationServerApi app, IDataStoreServerApi dss,
      String sessionToken) {
    this.app = app;
    this.dss = dss;
    this.sessionToken = sessionToken;
  }

  public String getSpaceOfProject(String projectCode) {
    ProjectSearchCriteria projectSearchCriteria = new ProjectSearchCriteria();
    projectSearchCriteria.withCode().thatEquals(projectCode);
    ProjectFetchOptions projectFetchOptions = new ProjectFetchOptions();
    projectFetchOptions.withSpace();
    SearchResult<Project> projects = app
        .searchProjects(sessionToken, projectSearchCriteria, projectFetchOptions);
    if (projects.getObjects().get(0) == null) {
      return null;
    }
    return projects.getObjects().get(0).getSpace().getCode();
  }

  public Date getProjectRegistrationDate(Project project) {
    return project.getRegistrationDate();
  }

  public List<Sample> getSamplesOfProject(Project project) {
    SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
    sampleSearchCriteria.withExperiment().withProject().withCode().thatEquals(project.getCode());
    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withType();
    fetchOptions.withProperties();
    SearchResult<Sample> samples = app
        .searchSamples(sessionToken, sampleSearchCriteria, fetchOptions);

    return samples.getObjects();
  }

  public String getSpeciesOfProject(Project project) {
    String species = "unknown";
    SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
    sampleSearchCriteria.withExperiment().withProject().withCode().thatEquals(project.getCode());
    sampleSearchCriteria.withType().withCode().thatEquals("Q_BIOLOGICAL_ENTITY");
    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withProperties();
    SearchResult<Sample> samples = app
        .searchSamples(sessionToken, sampleSearchCriteria, fetchOptions);

    if (samples.getTotalCount() > 0) {
      species = samples.getObjects().get(0).getProperties().get("Q_NCBI_ORGANISM");
      species = getTaxonomy(species);
    }

    if (species.equals("")) {
      species = "unkown";
    }
    return species;

  }

  public Set<String> getSampleTypesOfProject(Project project) {
    Set<String> sampleTypes = new HashSet<>();
    SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
    sampleSearchCriteria.withExperiment().withProject().withCode().thatEquals(project.getCode());
    sampleSearchCriteria.withType().withCode().thatEquals("Q_TEST_SAMPLE");
    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withProperties();
    SearchResult<Sample> samples = app
        .searchSamples(sessionToken, sampleSearchCriteria, fetchOptions);

    for (Sample sample : samples.getObjects()) {
      sampleTypes.add(sample.getProperty("Q_SAMPLE_TYPE"));
    }

    return sampleTypes;
  }


  public Date getFirstRegisteredDate(Project project) {
    SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
    sampleSearchCriteria.withExperiment().withProject().withCode().thatEquals(project.getCode());
    sampleSearchCriteria.withType().withCode().thatEquals("Q_NGS_SINGLE_SAMPLE_RUN");
    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withDataSets();
    SearchResult<Sample> samples = app
        .searchSamples(sessionToken, sampleSearchCriteria, fetchOptions);

    ArrayList<Date> datesRegistered = new ArrayList<>();
    for (int i = 0; i < samples.getObjects().size(); i++) {
      Sample rawDataSample = samples.getObjects().get(i);
      for (DataSet dataSet : rawDataSample.getDataSets()) {
        datesRegistered.add(dataSet.getRegistrationDate());
      }
    }

    Date firstRegistered = null;
    if (!datesRegistered.isEmpty()) {
      firstRegistered = Collections.min(datesRegistered);
    }

    return firstRegistered;
  }

  public Date getFirstAnalyzedDate(Project project) {
    SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
    sampleSearchCriteria.withExperiment().withProject().withCode().thatEquals(project.getCode());
    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withDataSets().withType();
    SearchResult<Sample> samples = app
        .searchSamples(sessionToken, sampleSearchCriteria, fetchOptions);

    ArrayList<Date> datesAnalyzed = new ArrayList<>();
    for (int i = 0; i < samples.getObjects().size(); i++) {
      Sample rawDataSample = samples.getObjects().get(i);
      for (DataSet dataSet : rawDataSample.getDataSets()) {
        if (dataSet.getType().getCode().contains("RESULT") || dataSet.getType().getCode()
            .contains("result")) {
          datesAnalyzed.add(dataSet.getRegistrationDate());
        }
      }
    }

    Date firstAnalyzed = null;
    if (!datesAnalyzed.isEmpty()) {
      firstAnalyzed = Collections.min(datesAnalyzed);
    }

    return firstAnalyzed;
  }

  public BeanItemContainer<ProjectBean> getListOfProjects() {
    if (projectBeanBeanItemContainer.size() > 0) {
      projectBeanBeanItemContainer.removeAllItems();
    }
    SearchResult<Project> projects = app
        .searchProjects(sessionToken, new ProjectSearchCriteria(), new ProjectFetchOptions());
    for (Project project : projects.getObjects()) {
      projectBeanBeanItemContainer.addBean(
          ProjectToProjectBeanConverter.convertToProjectBean(project));
    }

    return projectBeanBeanItemContainer;
  }

  public String getProjectDescription(String projectCode) {
    ProjectSearchCriteria projectSearchCriteria = new ProjectSearchCriteria();
    projectSearchCriteria.withCode().thatEquals(projectCode);
    ProjectFetchOptions projectFetchOptions = new ProjectFetchOptions();

    SearchResult<Project> projects = app
        .searchProjects(sessionToken, projectSearchCriteria, projectFetchOptions);

    return projects.getObjects().get(0).getDescription();
  }

  public String getTaxonomy(String ncbi_code) {
    String taxonomy = "Unknown";
    VocabularyTermSearchCriteria vocabularyTermSearchCriteria = new VocabularyTermSearchCriteria();
    vocabularyTermSearchCriteria.withCode().thatEquals(ncbi_code);
    SearchResult<VocabularyTerm> vocabularyTermSearchResult = app
        .searchVocabularyTerms(sessionToken, vocabularyTermSearchCriteria,
            new VocabularyTermFetchOptions());
    for (VocabularyTerm vocabularyTerm : vocabularyTermSearchResult.getObjects()) {
      if (vocabularyTerm.getCode().equals(ncbi_code)) {
        taxonomy = vocabularyTerm.getDescription();
      }
    }
    return taxonomy;
  }

  public Project getProjectByCode(String projectCode) {
    ProjectSearchCriteria projectSearchCriteria = new ProjectSearchCriteria();
    projectSearchCriteria.withCode().thatEquals(projectCode);
    ProjectFetchOptions projectFetchOptions = new ProjectFetchOptions();
    SearchResult<Project> projects = app
        .searchProjects(sessionToken, projectSearchCriteria, projectFetchOptions);
    if (projects.getObjects().get(0) == null) {
      return null;
    }
    return projects.getObjects().get(0);
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public IApplicationServerApi getApp() {
    return app;
  }

  public IDataStoreServerApi getDss() {
    return dss;
  }
}
