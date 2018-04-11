package life.qbic.module.projectFollowerModule;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import life.qbic.module.projectOverviewModule.ProjectOverviewGrid;
import org.vaadin.teemu.switchui.Switch;

/**
 * Created by sven1103 on 19/12/16.
 */
public class ProjectFollowerViewImpl implements ProjectFollowerView {

  private String spaceCaption;
  private String projectCaption;

  private ComboBox spaceBox;
  private ComboBox projectBox;

  private VerticalLayout mainContent;
  private HorizontalLayout centralWrapper;

  private HorizontalLayout boxWrapper;

  private Switch followSwitch;

  private Label descriptionField;


  public ProjectFollowerViewImpl() {
    spaceCaption = "ExampleCaption1";
    projectCaption = "ExampleCaption2";
  }

  @Override
  public ProjectOverviewGrid getProjectGrid() {
    return null;
  }

  @Override
  public ComboBox getSpaceComboBox() {
    return this.spaceBox;
  }

  @Override
  public ComboBox getProjectComboBox() {
    return this.projectBox;
  }

  @Override
  public ProjectFollowerView setSpaceCaption(String caption) {
    this.spaceCaption = caption;
    return this;
  }

  @Override
  public ProjectFollowerView setProjectCaption(String caption) {
    this.projectCaption = caption;
    return this;
  }

  @Override
  public Label getDescriptionField() {
    return this.descriptionField;
  }

  @Override
  public ProjectFollowerView build() {
    this.mainContent = new VerticalLayout();

    this.spaceBox = new ComboBox(spaceCaption);
    this.projectBox = new ComboBox(projectCaption);
    this.boxWrapper = new HorizontalLayout();
    this.centralWrapper = new HorizontalLayout();
    this.followSwitch = new Switch();
    this.descriptionField = new Label();

    this.descriptionField.setValue("No description available.");

    HorizontalLayout leftContainer = new HorizontalLayout();
    leftContainer.addComponent(spaceBox);

    HorizontalLayout rightContainer = new HorizontalLayout();
    rightContainer.addComponent(projectBox);

    followSwitch.setValue(false);
    followSwitch.setVisible(true);
    followSwitch.setAnimationEnabled(true);
    followSwitch.setEnabled(false);

    descriptionField.setWidthUndefined();
    descriptionField.addStyleName("slider-description-label");

    boxWrapper.addComponents(leftContainer, rightContainer);
    centralWrapper.setSpacing(true);
    centralWrapper.addComponent(boxWrapper);
    centralWrapper.addComponent(followSwitch);
    centralWrapper.addComponent(descriptionField);
    centralWrapper.setComponentAlignment(boxWrapper, Alignment.MIDDLE_CENTER);
    mainContent.addComponents(centralWrapper);
    boxWrapper.setSizeUndefined();
    centralWrapper.setSizeUndefined();
    centralWrapper.setComponentAlignment(followSwitch, Alignment.MIDDLE_CENTER);
    centralWrapper.setComponentAlignment(descriptionField, Alignment.MIDDLE_CENTER);
    mainContent.setComponentAlignment(centralWrapper, Alignment.TOP_CENTER);
    mainContent.setSpacing(true);
    mainContent.setMargin(true);
    boxWrapper.setMargin(true);
    boxWrapper.setSpacing(true);
    mainContent.setHeight("300px");
    return this;
  }

  @Override
  public Switch getFollowSwitch() {
    return this.followSwitch;
  }

  @Override
  public VerticalLayout getUI() {
    return this.mainContent;
  }
}
