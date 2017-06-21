package life.qbic.projectOverviewModule;

import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import life.qbic.database.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by sven on 11/13/16.
 * This class contains the business logic and is connected with the
 * MySQL database which contains all the information of QBiC projects.
 */
public class ProjectContentModel {

    private HashMap<String, String> queryArguments = new HashMap<>();

    {
        queryArguments.put("table", "projectsoverview");
    }

    private String primaryKey = "projectID";

    private double projectsWithClosedStatus;

    private double projectsWithInProgressStatus;

    private double projectsWithOpenStatus;

    private SQLContainer tableContent;

    private final ProjectDatabaseConnector projectDatabaseConnector;

    private HashMap<String, Double> keyFigures;

    private List<String> followingProjects;

    public ProjectContentModel(ProjectDatabaseConnector projectDatabaseConnector,
                               List followingProjects) {
        this.projectDatabaseConnector = projectDatabaseConnector;
        this.followingProjects = followingProjects;
    }

    public List<String> getFollowingProjects() {
        return this.followingProjects;
    }

    public final void init() throws SQLException, IllegalArgumentException, WrongArgumentSettingsException {
        projectDatabaseConnector.connectToDatabase();
        this.tableContent = projectDatabaseConnector.loadSelectedTableData(queryArguments.get("table"), primaryKey);
        querryKeyFigures();
        getProjectesTimeLineStats();
    }

    /**
     * Getter for the table content
     *
     * @return The table content
     */
    public final SQLContainer getTableContent() {
        return this.tableContent;
    }


    /**
     * Request the counts for the key figures of
     * the projects status 'open', 'in progress', 'closed'
     */
    private void querryKeyFigures() throws SQLException, WrongArgumentSettingsException {

        HashMap<String, Double> keyFigures = new HashMap<>();

        projectsWithOpenStatus = (double) projectDatabaseConnector.makeFreeFormQuery(QuerryType.PROJECTSTATUS_OPEN, queryArguments, primaryKey, followingProjects).getCount();
        System.out.println(projectsWithOpenStatus);
        projectsWithClosedStatus = (double) projectDatabaseConnector.makeFreeFormQuery(QuerryType.PROJECTSTATUS_CLOSED, queryArguments, primaryKey, followingProjects).getCount();
        System.out.println(projectsWithClosedStatus);
        projectsWithInProgressStatus = (double) projectDatabaseConnector.makeFreeFormQuery(QuerryType.PROJECTSTATUS_INPROGRESS, queryArguments, primaryKey, followingProjects).getCount();
        System.out.println(projectsWithInProgressStatus);

        keyFigures.put("closed", projectsWithClosedStatus);
        keyFigures.put("open", projectsWithOpenStatus);
        keyFigures.put("in progress", projectsWithInProgressStatus);
        this.keyFigures = keyFigures;
    }


    public void updateFigure() {
        try {
            querryKeyFigures();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void refresh() throws SQLException, WrongArgumentSettingsException {
        this.tableContent = projectDatabaseConnector.loadSelectedTableData(queryArguments.get("table"), primaryKey);
        querryKeyFigures();
    }


    public Map<String, Double> getKeyFigures() {
        return this.keyFigures;
    }

    /**
     * Request project timeline statistics
     *
     * @return A map containing values for different categories
     */
    public Map<String, Integer> getProjectesTimeLineStats() {
        final Map<String, Integer> projectsStats = new HashMap<>();

        if (tableContent == null) {
            return projectsStats;
        }

        writeNumberProjectsPerTimeIntervalFromStart(projectsStats);

        return projectsStats;

    }

    /**
     * Helper function that computes the project timeline statistics
     *
     * @param container map that is going to be filled with stats
     */
    private void writeNumberProjectsPerTimeIntervalFromStart(Map<String, Integer> container) {

        Collection<?> itemIds = tableContent.getItemIds();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Date> dateList = new ArrayList<>();

        container.put("0 to 2 weeks", 0);
        container.put("2 to 6 weeks", 0);
        container.put("6 to 12 weeks", 0);
        container.put("> 12 weeks", 0);

        for (Object itemId : itemIds) {
            String registeredDateCol = TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.RAWDATAREGISTERED);
            Property property = tableContent.getContainerProperty(itemId, registeredDateCol);

            try {
                Date registration = dateFormat.parse((property.getValue()).toString());
                if (registration != null) {
                    dateList.add(registration);
                }
            } catch (Exception exc) {
                //Do nothing
            }
        }

        Date currentDate = new Date();

        for (Date date : dateList) {
            long daysPassed = TimeUnit.DAYS.convert(currentDate.getTime() - date.getTime(), TimeUnit.MILLISECONDS);
            if (daysPassed / 7 < 2)
                container.put("0 to 2 weeks", container.get("0 to 2 weeks") + 1);
            else if (daysPassed / 7 < 6)
                container.put("2 to 6 weeks", container.get("2 to 6 weeks") + 1);
            else if (daysPassed / 7 < 12)
                container.put("6 to 12 weeks", container.get("6 to 12 weeks") + 1);
            else
                container.put("> 12 weeks", container.get("> 12 weeks") + 1);
        }
    }


}
