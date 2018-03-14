package life.qbic;

import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import life.qbic.database.*;
import org.apache.commons.logging.Log;

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

    private int unregisteredProjects, inTimeProjects, overdueProjects;

    private SQLContainer tableContent;

    private final ProjectDatabaseConnector projectDatabaseConnector;

    private HashMap<String, Double> keyFigures;

    private final Log log;

    private List<String> followingProjects;

    public ProjectContentModel(ProjectDatabaseConnector projectDatabaseConnector,
                               List followingProjects, Log log) {
        this.projectDatabaseConnector = projectDatabaseConnector;
        this.followingProjects = followingProjects;
        this.log = log;
    }

    public List<String> getFollowingProjects() {
        return this.followingProjects;
    }

    public final void init() throws SQLException, IllegalArgumentException, WrongArgumentSettingsException {
        projectDatabaseConnector.connectToDatabase();
        this.tableContent = projectDatabaseConnector.loadSelectedTableData(queryArguments.get("table"), primaryKey);
        if (getFollowingProjects().size() > 0) {
            querryKeyFigures();
            getProjectsTimeLineStats();
        }

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
        projectsWithClosedStatus = (double) projectDatabaseConnector.makeFreeFormQuery(QuerryType.PROJECTSTATUS_CLOSED, queryArguments, primaryKey, followingProjects).getCount();
        projectsWithInProgressStatus = (double) projectDatabaseConnector.makeFreeFormQuery(QuerryType.PROJECTSTATUS_INPROGRESS, queryArguments, primaryKey, followingProjects).getCount();

        keyFigures.put("closed", projectsWithClosedStatus);
        keyFigures.put("open", projectsWithOpenStatus);
        keyFigures.put("in progress", projectsWithInProgressStatus);
        this.keyFigures = keyFigures;
    }


    public void updateFigure() {
        try {
            if (getFollowingProjects().size() > 0) {
                querryKeyFigures();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void refresh()  {
        try {
            this.tableContent = projectDatabaseConnector.loadSelectedTableData(queryArguments.get("table"), primaryKey);
            if (getFollowingProjects().size() > 0) {
                querryKeyFigures();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WrongArgumentSettingsException e) {
            e.printStackTrace();
        }


    }


    public Map<String, Double> getKeyFigures() {
        return this.keyFigures;
    }

    /**
     * Request project timeline statistics
     *
     * @return A map containing values for different categories
     */
    public Map<String, Integer> getProjectsTimeLineStats() {
        LinkedHashMap<String, Integer> projectsStats = new LinkedHashMap<>();

        if (tableContent == null) {
            return projectsStats;
        }

        projectsStats = writeNumberProjectsPerTimeIntervalFromStart();

        return projectsStats;

    }


    private LinkedHashMap<String, Integer> writeNumberProjectsPerTimeIntervalFromStart() {

        LinkedHashMap<String, Integer> container = new LinkedHashMap<>();

        container.put("unregistered", 0);
        container.put("in time", 0);
        container.put("overdue", 0);

        if (getFollowingProjects().size() > 0) {
            Collection<?> itemIds = tableContent.getItemIds();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            List<Date> dateList = new ArrayList<>();

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
            unregisteredProjects = followingProjects.size()-dateList.size();
            container.put("unregistered", unregisteredProjects);
            for (Date date : dateList) {
                long daysPassed = TimeUnit.DAYS.convert(currentDate.getTime() - date.getTime(), TimeUnit.MILLISECONDS);
                if (daysPassed / 7 < 6)
                    container.put("in time", container.get("in time") + 1);
                else
                    container.put("overdue", container.get("overdue") + 1);
            }
            inTimeProjects = container.get("in time");
            overdueProjects = container.get("overdue");
        }
        return container;
    }

    public int getUnregisteredProjects() {
        return unregisteredProjects;
    }

    public int getInTimeProjects() {
        return inTimeProjects;
    }

    public int getOverdueProjects() {
        return overdueProjects;
    }
}
