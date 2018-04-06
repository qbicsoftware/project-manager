package life.qbic.projectSheetModule;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import java.util.List;
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

  public ProjectSheetPresenter(ProjectSheetView projectSheetView, OpenBisClient openbisClient, Log log) {
    this.projectSheetView = projectSheetView;
    this.log = log;
    this.informationCommittedFlag = new ObjectProperty<>(false);
    this.openBisClient = openbisClient;
    init();
  }

  private void init() {

  }

  public void showInfoForProject(Item project) {

    if (project == null) {
      projectSheetView.setDefaultContent();
    } else {
      projectSheetView.getProjectSheet().removeAllComponents();
      currentItem = project;
      projectSheetView.getProjectSheet()
          .setCaption(currentItem.getItemProperty("projectID").getValue().toString());
      projectSheetView.showProjectLayout();
      projectSheetView.getProjectSheet().addComponent(getDescription());
      projectSheetView.getProjectSheet().addComponent(getSpecies());
    }

  }

  public Label getSpecies(){
    String species = "Unknown";
    String identifier = openBisClient.getProjectByCode(currentItem.getItemProperty("projectID").getValue().toString()).getIdentifier();
    System.out.println(openBisClient.getProjectByIdentifier(identifier).getDescription());
    for (Sample sample : openBisClient.getSamplesOfProject(identifier)) {
      if (sample.getSampleTypeCode().equals("Q_BIOLOGICAL_ENTITY")) {
        species = sample.getProperties().get("Q_NCBI_ORGANISM");
      }
    }
    Label label = new Label(species);
    return label;
  }

  public Label getDescription(){
    String identifier = openBisClient.getProjectByCode(currentItem.getItemProperty("projectID").getValue().toString()).getIdentifier();
    String description = openBisClient.getProjectByIdentifier(identifier).getDescription();
    Label label = new Label(description);
    return label;
  }

  public ObjectProperty<Boolean> getInformationCommittedFlag() {
    return this.informationCommittedFlag;
  }

  public ProjectSheetView getProjectSheetView() {
    return projectSheetView;
  }


}
