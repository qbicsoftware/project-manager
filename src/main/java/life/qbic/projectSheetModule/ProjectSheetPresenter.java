package life.qbic.projectSheetModule;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import life.qbic.database.ColumnTypes;
import life.qbic.database.ProjectDatabaseConnector;
import life.qbic.database.TableColumns;
import org.apache.commons.logging.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sven1103 on 10/01/17.
 */
public class ProjectSheetPresenter {

    private final ProjectSheetView projectSheetView;
    private final Log log;
    private final ObjectProperty<Boolean> informationCommittedFlag;
    private Item currentItem;

    public ProjectSheetPresenter(ProjectSheetView projectSheetView, Log log) {
        this.projectSheetView = projectSheetView;
        this.log = log;
        this.informationCommittedFlag = new ObjectProperty<>(false);
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
            projectSheetView.getProjectSheet().setCaption(currentItem.getItemProperty("projectID").getValue().toString());
            projectSheetView.showProjectLayout();
        }

    }


    public ObjectProperty<Boolean> getInformationCommittedFlag() {
        return this.informationCommittedFlag;
    }

    public ProjectSheetView getProjectSheetView() {
        return projectSheetView;
    }


}
