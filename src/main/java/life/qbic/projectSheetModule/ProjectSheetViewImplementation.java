package life.qbic.projectSheetModule;


import com.vaadin.server.FontAwesome;
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
    projectSheet.setSizeFull();
    init();
  }

  private void init() {
    projectSheet.removeAllComponents();
    setDefaultContent();
  }

  @Override
  public VerticalLayout getProjectSheet() {
    return projectSheet;
  }

  @Override
  public void setDefaultContent() {
    projectSheet.removeAllComponents();
    projectSheet.setCaption("Click a project in the " +
        "table to get detailed content here!");
    projectSheet.setVisible(false);
  }

}
