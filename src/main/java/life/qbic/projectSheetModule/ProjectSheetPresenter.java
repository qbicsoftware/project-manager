package life.qbic.projectSheetModule;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import life.qbic.Utils;
import life.qbic.database.ColumnTypes;
import life.qbic.database.ProjectDatabaseConnector;
import life.qbic.database.TableColumns;
import life.qbic.database.WrongArgumentSettingsException;
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

    private final ProjectDatabaseConnector dbConnector;

    private final String sqlRegistrationDateCommitCommand = String.format("UPDATE projectsoverview SET %s = ? " +
                    "WHERE %s = ?",
            TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.REGISTRATIONDATE),
            TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.ID));

    private final String sqlBarcodeSentDateCommitCommand = String.format("UPDATE projectsoverview SET %s = ? " +
                    "WHERE %s = ?",
            TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.BARCODESENTDATE),
            TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.ID));

    private Item currentItem;

    private final Log log;

    private final ObjectProperty<Boolean> informationCommittedFlag;

    public ProjectSheetPresenter(ProjectSheetView projectSheetView, ProjectDatabaseConnector dbConnector, Log log) {
        this.projectSheetView = projectSheetView;
        this.dbConnector = dbConnector;
        this.log = log;
        this.informationCommittedFlag = new ObjectProperty<>(false);
        init();
    }

    private void init() {
        projectSheetView.getSaveButton().addClickListener(event -> {
            try {
                commitChangesToDataBase();
            } catch (SQLException exc) {
                log.fatal("Could not update saved changes to database", exc);
            } catch (NullPointerException e) {
                log.info("One or more fields where empty and have not been saved to the database");
            }
        });
    }

    public void showInfoForProject(Item project) {

        if (project == null) {
            projectSheetView.setDefaultContent();
        } else {
            currentItem = project;
            fillInContentFromItem();
            projectSheetView.showProjectLayout();
        }

    }

    private void fillInContentFromItem() {
        String projectCode = null;
        Date projectRegistered = null;
        Date barcodeSent = null;

        try {
            projectCode = (String) currentItem.getItemProperty(
                    TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID)).getValue();
            projectRegistered = (Date) currentItem.getItemProperty(
                    TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.REGISTRATIONDATE)).getValue();
            barcodeSent = (Date) currentItem.getItemProperty(
                    TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.BARCODESENTDATE)).getValue();
        } catch (NullPointerException exp) {
            // Do nothing
        } finally {
            projectSheetView.setProjectCode(projectCode);
            projectSheetView.setRegistrationDate(projectRegistered);
            projectSheetView.setBarcodeSentDate(barcodeSent);
        }
    }

    private void commitChangesToDataBase() throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String id = currentItem.getItemProperty(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.ID)).getValue().toString();
        String projectRegisteredDate = dateFormat.format(projectSheetView.getRegistrationDateField().getValue());
        String project = (String) currentItem.getItemProperty(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID)).getValue();

        Connection con_registrationDate = dbConnector.getConnectionPool().reserveConnection();

        if (con_registrationDate == null) {
            throw new SQLException("Could not reserve any connection to the database.");

        }

        PreparedStatement ps_registrationDate = con_registrationDate.prepareStatement(sqlRegistrationDateCommitCommand);

        ps_registrationDate.setString(1, projectRegisteredDate);
        ps_registrationDate.setString(2, id);

        ps_registrationDate.execute();
        con_registrationDate.commit();

        ps_registrationDate.close();
        con_registrationDate.close();
        log.info(String.format("Registration date for project %s successfully updated.", project));

        String barcodeSentDate = dateFormat.format(projectSheetView.getBarcodeSentDateField().getValue());

        Connection con_barcodeDate = dbConnector.getConnectionPool().reserveConnection();

        if (con_barcodeDate == null) {
            throw new SQLException("Could not reserve any connection to the database.");

        }

        PreparedStatement ps_barcodeDate = con_barcodeDate.prepareStatement(sqlBarcodeSentDateCommitCommand);

        ps_barcodeDate.setString(1, barcodeSentDate);
        ps_barcodeDate.setString(2, id);

        ps_barcodeDate.execute();
        con_barcodeDate.commit();

        ps_barcodeDate.close();
        con_barcodeDate.close();
        log.info(String.format("Barcode date for project %s successfully updated.", project));
        informationCommittedFlag.setValue(!informationCommittedFlag.getValue());

    }

    public ObjectProperty<Boolean> getInformationCommittedFlag() {
        return this.informationCommittedFlag;
    }

    public ProjectSheetView getProjectSheetView() {
        return projectSheetView;
    }


}
