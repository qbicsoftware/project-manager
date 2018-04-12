package life.qbic.module.projectSheetModule;


import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by sven1103 on 9/01/17.
 */
public class ProjectSheetViewImplementation implements ProjectSheetView {

  private VerticalLayout projectSheet;


  public ProjectSheetViewImplementation() {
    this.projectSheet = new VerticalLayout();
    projectSheet.setIcon(FontAwesome.INFO_CIRCLE);
    projectSheet.setWidth("400px");
    init();
  }

  private void init() {
    reset();
  }

  @Override
  public VerticalLayout getProjectSheet() {
    return projectSheet;
  }

  @Override
  public void reset() {
    projectSheet.removeAllComponents();
    projectSheet.setCaption("Project Details: Click on a project in the table.");
  }


}
