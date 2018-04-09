package life.qbic.projectSheetModule;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
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
  private String species;
  private String samples, description;

  public ProjectSheetPresenter(ProjectSheetView projectSheetView, OpenBisClient openbisClient,
      Log log) {
    this.projectSheetView = projectSheetView;
    this.log = log;
    this.informationCommittedFlag = new ObjectProperty<>(false);
    this.openBisClient = openbisClient;
    init();
  }

  private void init() {
    Map<String, String> taxMap = openBisClient.getVocabCodesAndLabelsForVocab("Q_NCBI_TAXONOMY");
    for (String key : taxMap.keySet()) {
      taxMapInversed.put(taxMap.get(key), key);
    }
  }

  public void showInfoForProject(Item project) {

    if (project == null) {
      projectSheetView.setDefaultContent();
    } else {
      projectSheetView.getProjectSheet().removeAllComponents();
      currentItem = project;
      projectSheetView.getProjectSheet()
          .setCaption("Project Details");
      projectSheetView.getProjectSheet().addComponent(getProject());
      projectSheetView.getProjectSheet().addComponent(getProjectDetail());
      projectSheetView.getProjectSheet().setVisible(true);
    }

  }

  public Label getProject() {
    String project = currentItem.getItemProperty("projectID").getValue().toString();
    Label label = new Label(project);
    label.setStyleName(ValoTheme.LABEL_COLORED);
    label.addStyleName(ValoTheme.LABEL_H2);
    return label;
  }

  public void loadInfo() {
    try {
      species = currentItem.getItemProperty("species").getValue().toString();
      samples = currentItem.getItemProperty("samples").getValue().toString();
      description = currentItem.getItemProperty("description").getValue().toString();
    } catch (NullPointerException ex) {
      species = "Unknown";
    }
  }

  public Label getProjectDetail() {
    loadInfo();
    Label label = new Label(
            "<ul>"+
            "  <li><b><font color=\"#007ae4\">Species: </b></font>" + species + "</li>"+
            "  <li><b><font color=\"#007ae4\">Samples: </b></font>" + samples + "</li>"+
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


}
