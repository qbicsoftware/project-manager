package life.qbic.module.projectSheetModule;


import com.vaadin.server.FontAwesome;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * Created by sven1103 on 9/01/17.
 */
public class ProjectSheetViewImplementation implements ProjectSheetView {

  private VerticalLayout projectSheet;
  private Window subWindow;


  public ProjectSheetViewImplementation() {
    this.projectSheet = new VerticalLayout();

    projectSheet.setIcon(FontAwesome.INFO_CIRCLE);
    projectSheet.setMargin(true);
    projectSheet.setSpacing(true);
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

  @Override
  public void createSubWindow() {
    subWindow = new Window("Project Details");
    projectSheet.setSizeFull();
    subWindow.setContent(projectSheet);
    subWindow.center();
    subWindow.setModal(true);
    subWindow.setWidth("70%");
    subWindow.setHeight("100%");


    //Somehow two windows open. This is a quick workaround.
    subWindow.addCloseListener((CloseListener) e -> {
      for (Window window : UI.getCurrent().getWindows()) {
        UI.getCurrent().getUI().removeWindow(window);
      }
    });
    UI.getCurrent().addWindow(subWindow);
  }
}
