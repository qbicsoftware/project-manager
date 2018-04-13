package life.qbic.module.projectSheetModule;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import life.qbic.openbis.openbisclient.OpenBisClient;
import org.apache.commons.logging.Log;

/**
 * Created by sven1103 on 10/01/17.
 */
public class ProjectSheetPresenter {

  private final ProjectSheetView projectSheetView;
  private final Log log;
  private final ObjectProperty<Boolean> informationCommittedFlag;
  private Item currentItem;
  private OpenBisClient openBisClient;
  private Map<String, String> taxMapInversed = new HashMap<>();

  public ProjectSheetPresenter(ProjectSheetView projectSheetView, OpenBisClient openbisClient,
      Log log) {
    this.projectSheetView = projectSheetView;
    this.log = log;
    this.informationCommittedFlag = new ObjectProperty<>(false);
    this.openBisClient = openbisClient;
    init();
  }

  public void init() {
    Map<String, String> taxMap = openBisClient.getVocabCodesAndLabelsForVocab("Q_NCBI_TAXONOMY");
    for (String key : taxMap.keySet()) {
      taxMapInversed.put(taxMap.get(key), key);
    }
  }

  public void showInfoForProject(Item project) {

    if (project == null) {
      projectSheetView.reset();
    } else {
      projectSheetView.reset();
      currentItem = project;
      projectSheetView.getProjectSheet()
          .setCaption("Project Details");
      projectSheetView.getProjectSheet().addComponent(getProject());
      projectSheetView.getProjectSheet().addComponent(getDescription());
      projectSheetView.getProjectSheet().addComponent(getProjectDetail());
      //ProjectInfoDownloader projectInfoDownloader = new ProjectInfoDownloader(project);
      HorizontalLayout bottomLayout = new HorizontalLayout();
      bottomLayout.setSpacing(true);
      bottomLayout.addComponents(getProjectTime(), getExportButton());
      projectSheetView.getProjectSheet().addComponent(bottomLayout);
    }

  }

  public Label getProject() {
    String project = currentItem.getItemProperty("projectID").getValue().toString();
    Label label = new Label(project);
    label.setStyleName(ValoTheme.LABEL_COLORED);
    label.addStyleName(ValoTheme.LABEL_H2);
    return label;
  }

  private Label getProjectTime() {
    String project = currentItem.getItemProperty("projectTime").getValue().toString();
    Label label = new Label(project);
    label.addStyleName(ValoTheme.LABEL_SMALL);
    if (label.getValue().equals("overdue")) {
      label.setStyleName("red");
    } else if (label.getValue().equals("unregistered")) {
      label.setStyleName("orange");
    } else if (label.getValue().equals("in time")) {
      label.setStyleName("green");
    }
    return label;
  }

  public Label getDescription() {
    String description = currentItem.getItemProperty("description").getValue().toString();
    Label label = new Label(description);
    return label;
  }

  private Label getProjectDetail() {
    String pi, species, samples;
    try {
      pi = currentItem.getItemProperty("investigatorName").getValue().toString();
    } catch (NullPointerException ex) {
      pi = "Unknown";
    }

    try {
      species = currentItem.getItemProperty("species").getValue().toString();
    } catch (NullPointerException ex) {
      species = "Unknown";
    }

    try {
      samples = currentItem.getItemProperty("samples").getValue().toString();
    } catch (NullPointerException ex) {
      samples = "Unknown";
    }

    Label label = new Label(
        "<ul>" +
            "  <li><b><font color=\"#007ae4\">PI: </b></font>" + pi + "</li>" +
            "  <li><b><font color=\"#007ae4\">Species: </b></font>" + species + "</li>" +
            "  <li><b><font color=\"#007ae4\">Samples: </b></font>" + samples + "</li>" +
            "</ul> ",
        ContentMode.HTML);
    return label;
  }

  public ObjectProperty<Boolean> getInformationCommittedFlag() {
    return this.informationCommittedFlag;
  }

  public ProjectSheetView getProjectSheetView() {
    return projectSheetView;
  }

  private Button getExportButton() {
    String fileName = currentItem.getItemProperty("projectID").getValue().toString();
    String projectName =
        "Project: " + currentItem.getItemProperty("projectID").getValue().toString();
    String projectStatus =
        "Status: " + currentItem.getItemProperty("projectTime").getValue().toString();
    String projectDescription = "Description: " + currentItem.getItemProperty("description");
    String projectPI = "PI: " + currentItem.getItemProperty("investigatorName");
    String projectSpecies = "Species: " + currentItem.getItemProperty("species");
    String projectSamples = "Samples: " + currentItem.getItemProperty("samples");

    try {
      File projectFile = File.createTempFile(fileName, ".txt");
      FileWriter fw = new FileWriter(projectFile);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(projectName);
      bw.newLine();
      bw.write(projectStatus);
      bw.newLine();
      bw.write(projectDescription);
      bw.newLine();
      bw.write(projectPI);
      bw.newLine();
      bw.write(projectSpecies);
      bw.newLine();
      bw.write(projectSamples);
      bw.close();
      fw.close();
      FileResource res = new FileResource(projectFile);
      FileDownloader fd = new FileDownloader(res);
      Button downloadButton = new Button("Summary");
      downloadButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
      fd.extend(downloadButton);
      return downloadButton;
    } catch (IOException e) {
      return null;
    }
  }
}
