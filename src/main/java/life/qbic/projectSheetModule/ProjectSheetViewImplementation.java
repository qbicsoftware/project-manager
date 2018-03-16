package life.qbic.projectSheetModule;


import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;

/**
 * Created by sven1103 on 9/01/17.
 */
public class ProjectSheetViewImplementation implements ProjectSheetView {

    private VerticalLayout projectSheet;

    private String projectCode;

    private Label projectLabel;

    public ProjectSheetViewImplementation(String projectCode) {
        this.projectCode = projectCode;
        this.projectSheet = new VerticalLayout();
        projectSheet.setIcon(FontAwesome.INFO_CIRCLE);
        this.projectLabel = new Label();
        projectSheet.setSizeUndefined();
        init();
    }

    private void init() {
        projectSheet.removeAllComponents();
        setDefaultContent();
        projectCode = "";
    }

    @Override
    public VerticalLayout getProjectSheet() {
        return projectSheet;
    }

    @Override
    public void setDefaultContent() {
        projectSheet.removeAllComponents();
        projectLabel = new Label("Click a project in the " +
                "table to get detailed content here!");
        projectSheet.addComponent(projectLabel);
    }

    @Override
    public void setProjectCode(String id) {
        this.projectCode = id;
    }

    @Override
    public void showProjectLayout() {
        projectLabel.setCaption(projectCode);
    }


}
