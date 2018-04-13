package life.qbic;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import life.qbic.connection.database.projectInvestigatorDB.ProjectDatabaseConnector;
import life.qbic.connection.database.projectInvestigatorDB.WrongArgumentSettingsException;
import life.qbic.connection.database.userManagementDB.UserManagementDB;
import life.qbic.openbis.openbisclient.OpenBisClient;
import org.apache.commons.logging.Log;

/**
 * Created by sven on 11/13/16. This class contains the business logic and is connected with the
 * MySQL database which contains all the information of QBiC projects.
 */
public class ProjectContentModel {

  private final ProjectDatabaseConnector projectDatabaseConnector;
  private final Log log;
  private HashMap<String, String> queryArguments = new HashMap<>();
  private String primaryKey = "projectID";
  private int unregisteredProjects, inTimeProjects, overdueProjects;
  private SQLContainer tableContent;
  private List<String> followingProjects;
  private OpenBisClient openBisClient;
  private UserManagementDB userManagementDB;
  private Map<String, String> taxMapInversed = new HashMap<>();
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  {
    queryArguments.put("table", "projectsoverview");
  }

  public ProjectContentModel(ProjectDatabaseConnector projectDatabaseConnector,
      UserManagementDB userManagementDB,
      List followingProjects, Log log, OpenBisClient openbisClient) {
    this.projectDatabaseConnector = projectDatabaseConnector;
    this.followingProjects = followingProjects;
    this.log = log;
    this.openBisClient = openbisClient;
    this.userManagementDB = userManagementDB;
    Map<String, String> taxMap = openBisClient.getVocabCodesAndLabelsForVocab("Q_NCBI_TAXONOMY");
    for (String key : taxMap.keySet()) {
      taxMapInversed.put(taxMap.get(key), key);
    }
  }

  public List<String> getFollowingProjects() {
    return this.followingProjects;
  }

  public final void init()
      throws SQLException, IllegalArgumentException, WrongArgumentSettingsException {
    projectDatabaseConnector.connectToDatabase();
    this.tableContent = projectDatabaseConnector
        .loadSelectedTableData(queryArguments.get("table"), primaryKey);
    if (getFollowingProjects().size() > 0) {
      update();
    }
  }

  /**
   * Getter for the table content
   *
   * @return The table content
   */
  public final SQLContainer getTableContent() {
    return this.tableContent;
  }


  public void update() {
    try {
      if (getFollowingProjects().size() > 0) {
        writeInfos();
        writeProjectStatus();
      }
    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }

  public void refresh() {
    try {
      this.tableContent = projectDatabaseConnector
          .loadSelectedTableData(queryArguments.get("table"), primaryKey);
      if (getFollowingProjects().size() > 0) {
        update();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Request project timeline statistics
   *
   * @return A map containing values for different categories
   */
  public Map<String, Integer> getProjectsTimeLineStats() {
    LinkedHashMap<String, Integer> projectsStats = new LinkedHashMap<>();

    if (tableContent == null) {
      return projectsStats;
    }

    projectsStats = writeNumberProjectsPerTimeIntervalFromStart();

    return projectsStats;

  }

  private void writeInfos() {
    if (getFollowingProjects().size() > 0) {
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
        String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue()
            .toString();
        String projectIdentifier = openBisClient.getProjectByCode(projectID).getIdentifier();
        writeProjectDate(itemId, projectIdentifier);
        writeDataAnalyzedDate(itemId, projectIdentifier);
        writeRawDate(itemId, projectIdentifier);
        writeProjectPI(itemId, projectID);
        writeFurtherInfo(itemId, projectIdentifier);
      }
    }
  }

  private void writeProjectDate(Object itemId, String projectIdentifier) {
    if (tableContent.getContainerProperty(itemId, "projectRegisteredDate")
        .getValue() == null) {
      tableContent.getContainerProperty(itemId, "projectRegisteredDate")
          .setValue(openBisClient.getProjectByIdentifier(projectIdentifier).getRegistrationDetails()
              .getRegistrationDate());
    }
  }

  private void writeDataAnalyzedDate(Object itemId, String projectIdentifier) {
      List<DataSet> dataSets = openBisClient
          .getDataSetsOfProjectByIdentifier(projectIdentifier);
      List<Date> resultDates = new ArrayList<>();
      for (DataSet ds : dataSets) {
        if (ds.getDataSetTypeCode().contains("RESULT") ||
            ds.getDataSetTypeCode().contains("result")) {
          resultDates.add(ds.getRegistrationDate());
        }
        HashMap<String, String> properties = ds.getProperties();
        String attachment_type = properties.get("Q_ATTACHMENT_TYPE");
        if (attachment_type != null) {
          if (attachment_type.equals("RESULT")) {
            resultDates.add(ds.getRegistrationDate());
          }
        }
      }
      if (!resultDates.isEmpty()) {
        Date lastResult = Collections.min(resultDates);
        tableContent.getContainerProperty(itemId, "dataAnalyzedDate").setValue(lastResult);
      }
  }


  public void writeFurtherInfo(Object itemId, String projectIdentifier) {
    String species = null;
    int sample_counter = 0;
    if (tableContent.getContainerProperty(itemId, "description")
        .getValue() == null) {
      String description = openBisClient.getProjectByIdentifier(projectIdentifier).getDescription();
      tableContent.getContainerProperty(itemId, "description").setValue(description);
    }
    if (tableContent.getContainerProperty(itemId, "samples")
        .getValue() == null || (tableContent.getContainerProperty(itemId, "species")
        .getValue() == null)) {
      for (Sample sample : openBisClient.getSamplesOfProject(projectIdentifier)) {
        if (sample.getSampleTypeCode().equals("Q_BIOLOGICAL_ENTITY")) {
          species = sample.getProperties().get("Q_NCBI_ORGANISM");
        }
        if (!sample.getSampleTypeCode().equals("Q_BIOLOGICAL_ENTITY") &&
            !sample.getSampleTypeCode().equals("Q_BIOLOGICAL_SAMPLE") &&
            !sample.getSampleTypeCode().equals("Q_TEST_SAMPLE") &&
            !sample.getSampleTypeCode().equals("Q_ATTACHMENT_SAMPLE")) {
          sample_counter = sample_counter + 1;
        }
      }
      species = taxMapInversed.get(species);
      tableContent.getContainerProperty(itemId, "species").setValue(species);
      tableContent.getContainerProperty(itemId, "samples").setValue(sample_counter);
    }
  }


  private void writeRawDate(Object itemId, String projectIdentifier) {
      try {
        List<DataSet> dataSets = openBisClient
            .getDataSetsOfProjectByIdentifier(projectIdentifier);
        List<Date> rawDates = new ArrayList<>();
        for (DataSet ds : dataSets) {
          if (!ds.getDataSetTypeCode().contains("RESULT") ||
              !ds.getDataSetTypeCode().contains("result")) {
            rawDates.add(ds.getRegistrationDate());
          }
          HashMap<String, String> properties = ds.getProperties();
          String attachment_type = properties.get("Q_ATTACHMENT_TYPE");
          if (attachment_type != null) {
            if (!attachment_type.equals("RESULT")) {
              rawDates.add(ds.getRegistrationDate());
            }
          }
        }
        if (!rawDates.isEmpty()) {
          Date lastResult = Collections.min(rawDates);
          tableContent.getContainerProperty(itemId, "rawDataRegistered").setValue(lastResult);
        }
      } catch (IndexOutOfBoundsException ex) {
        tableContent.getContainerProperty(itemId, "rawDataRegistered").setValue(null);

      }
  }

  public void writeProjectStatus() {
    if (getFollowingProjects().size() > 0) {
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
        if (tableContent.getContainerProperty(itemId, "rawDataRegistered").getValue() == null) {
          tableContent.getContainerProperty(itemId, "projectTime").setValue("unregistered");
        } else {
          try {
            Date currentDate = new Date();
            Date registration = dateFormat.parse(
                tableContent.getContainerProperty(itemId, "rawDataRegistered").getValue()
                    .toString());
            Date analyzed = dateFormat.parse(
                tableContent.getContainerProperty(itemId, "dataAnalyzedDate").getValue()
                    .toString());
            long daysPassed = TimeUnit.DAYS
                .convert(currentDate.getTime() - registration.getTime(), TimeUnit.MILLISECONDS);
            long daysFromRegToAnalisis = TimeUnit.DAYS
                .convert(analyzed.getTime() - registration.getTime(), TimeUnit.MILLISECONDS);
            if ((daysPassed / 7 < 6) || (daysFromRegToAnalisis / 7 < 6)) {
              tableContent.getContainerProperty(itemId, "projectTime").setValue("in time");
            } else {
              tableContent.getContainerProperty(itemId, "projectTime").setValue("overdue");
            }
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

  private void writeProjectPI(Object itemId, String projectID) {
    if (tableContent.getContainerProperty(itemId, "investigatorName")
        .getValue() == null) {
      tableContent.getContainerProperty(itemId, "investigatorName")
          .setValue(userManagementDB.getProjectPI(projectID));
    }
  }

  private LinkedHashMap<String, Integer> writeNumberProjectsPerTimeIntervalFromStart() {

    LinkedHashMap<String, Integer> container = new LinkedHashMap<>();
    unregisteredProjects = 0;
    inTimeProjects = 0;
    overdueProjects = 0;
    for (Object itemId : tableContent.getItemIds()) {
      if (tableContent.getContainerProperty(itemId, "projectTime").getValue()
          .equals("unregistered")) {
        unregisteredProjects = unregisteredProjects + 1;
      } else if (tableContent.getContainerProperty(itemId, "projectTime").getValue()
          .equals("in time")) {
        inTimeProjects = inTimeProjects + 1;
      } else if (tableContent.getContainerProperty(itemId, "projectTime").getValue()
          .equals("overdue")) {
        overdueProjects = overdueProjects + 1;
      }
    }

    container.put("unregistered", unregisteredProjects);
    container.put("in time", inTimeProjects);
    container.put("overdue", overdueProjects);

    return container;
  }


  public int getUnregisteredProjects() {
    return unregisteredProjects;
  }

  public int getInTimeProjects() {
    return inTimeProjects;
  }

  public int getOverdueProjects() {
    return overdueProjects;
  }
}
