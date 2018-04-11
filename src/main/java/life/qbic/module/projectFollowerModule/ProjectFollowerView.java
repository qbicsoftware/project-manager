package life.qbic.module.projectFollowerModule;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import life.qbic.module.projectOverviewModule.ProjectOverviewGrid;
import org.vaadin.teemu.switchui.Switch;

/**
 * Created by sven on 12/18/16.
 */
public interface ProjectFollowerView {

  ProjectOverviewGrid getProjectGrid();

  ComboBox getSpaceComboBox();

  ComboBox getProjectComboBox();

  ProjectFollowerView setSpaceCaption(String caption);

  ProjectFollowerView setProjectCaption(String caption);

  Label getDescriptionField();

  ProjectFollowerView build();

  Switch getFollowSwitch();

  VerticalLayout getUI();
}
