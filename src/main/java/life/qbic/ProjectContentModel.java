package life.qbic;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import life.qbic.database.projectInvestigatorDB.ColumnTypes;
import life.qbic.database.projectInvestigatorDB.ProjectDatabaseConnector;
import life.qbic.database.projectInvestigatorDB.TableColumns;
import life.qbic.database.projectInvestigatorDB.WrongArgumentSettingsException;
import life.qbic.database.userManagementDB.UserManagementDB;
import life.qbic.openbis.openbisclient.OpenBisClient;
import org.apache.commons.logging.Log;

/**
 * Created by sven on 11/13/16. This class contains the business logic and is connected with the
 * MySQL database which contains all the information of QBiC projects.
 */
public class ProjectContentModel {

  private final ProjectDatabaseConnector projectDatabaseConnector;
  private final Log log;
  private HashMap<String, String> queryArguments = new HashMap<>();
  private String primaryKey = "projectID";
  private int unregisteredProjects, inTimeProjects, overdueProjects;
  private SQLContainer tableContent;
  private List<String> followingProjects;
  private OpenBisClient openBisClient;
  private UserManagementDB userManagementDB;

  {
    queryArguments.put("table", "projectsoverview");
  }

  public ProjectContentModel(ProjectDatabaseConnector projectDatabaseConnector, UserManagementDB userManagementDB,
      List followingProjects, Log log, OpenBisClient openbisClient) {
    this.projectDatabaseConnector = projectDatabaseConnector;
    this.followingProjects = followingProjects;
    this.log = log;
    this.openBisClient = openbisClient;
    this.userManagementDB = userManagementDB;
  }

  public List<String> getFollowingProjects() {
    return this.followingProjects;
  }

  public final void init()
      throws SQLException, IllegalArgumentException, WrongArgumentSettingsException {
    projectDatabaseConnector.connectToDatabase();
    this.tableContent = projectDatabaseConnector
        .loadSelectedTableData(queryArguments.get("table"), primaryKey);
    if (getFollowingProjects().size() > 0) {
      update();
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



  public void update() {
    try {
      if (getFollowingProjects().size() > 0) {
        writeProjectDate();
        writeProjectPI();
        writeRawDate();
        writeDataAnalyzedDate();
      }
    } catch (Exception exp) {
      exp.printStackTrace();
    }
  }

  public void refresh() {
    try {
      this.tableContent = projectDatabaseConnector
          .loadSelectedTableData(queryArguments.get("table"), primaryKey);
      if (getFollowingProjects().size() > 0) {
        update();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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

  private void writeProjectDate() {
    int counter = 0;
    log.info("Update project registration dates");
    if (getFollowingProjects().size() > 0){
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
      if (tableContent.getContainerProperty(itemId, "projectRegisteredDate").getValue() == null) {
         String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue()
            .toString();
        counter = counter + 1;
          tableContent.getContainerProperty(itemId, "projectRegisteredDate")
              .setValue(openBisClient.getProjectByCode(projectID).getRegistrationDetails().getRegistrationDate());
        }
      }
    }
    log.info(counter + " Projects updated.");
  }

  private void writeDataAnalyzedDate() {
    int counter = 0;
    log.info("Update data analyzed date");
    if (getFollowingProjects().size() > 0){
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
        if (tableContent.getContainerProperty(itemId, "dataAnalyzedDate").getValue() == null) {
          String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue()
              .toString();
          counter = counter + 1;
          String projectIdentifier = openBisClient.getProjectByCode(projectID).getIdentifier();
          List<DataSet> dataSets = openBisClient.getDataSetsOfProjectByIdentifier(projectIdentifier);
          List<Date> resultDates = new ArrayList<>();
          for (DataSet ds  : dataSets) {
            if (ds.getDataSetTypeCode().contains("RESULTS")) {
              resultDates.add(ds.getRegistrationDate());
            }
          }
          if (!resultDates.isEmpty()) {
            Date lastResult = Collections.max(resultDates);
            tableContent.getContainerProperty(itemId, "dataAnalyzedDate").setValue(lastResult);
          }
        }
      }
    }
    log.info(counter + " Projects updated.");
  }

  private void writeRawDate() {
    int counter = 0;
    log.info("Update raw data registration dates");
    if (getFollowingProjects().size() > 0){
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
        if (tableContent.getContainerProperty(itemId, "rawDataRegistered").getValue() == null) {
          String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue()
            .toString();
          counter = counter + 1;
          String projectIdentifier = openBisClient.getProjectByCode(projectID).getIdentifier();
          try {
            tableContent.getContainerProperty(itemId, "rawDataRegistered")
                .setValue(openBisClient.getDataSetsOfProjectByIdentifier(projectIdentifier).get(0)
                    .getRegistrationDate());
          } catch (IndexOutOfBoundsException ex) {
            tableContent.getContainerProperty(itemId, "rawDataRegistered").setValue(null);
          }
        }
      }
    }
    log.info(counter + " Projects updated.");
  }

  private void writeProjectPI() {
    log.info("Update project PIs");
    int counter = 0;
    if (getFollowingProjects().size() > 0) {
      Collection<?> itemIds = tableContent.getItemIds();
      for (Object itemId : itemIds) {
       if (tableContent.getContainerProperty(itemId, "investigatorName").getValue() == null || tableContent.getContainerProperty(itemId, "investigatorName").getValue().equals("")) {
         counter = counter + 1;
         String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue()
            .toString();
          tableContent.getContainerProperty(itemId, "investigatorName").setValue(userManagementDB.getProjectPI(projectID));
        }
      }
    }
    log.info(counter + " Projects updated.");
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
        Property rawReg = tableContent.getContainerProperty(itemId, registeredDateCol);



        try {
          Date registration = dateFormat.parse(rawReg.getValue().toString());
          if (registration != null) {
            dateList.add(registration);
          }
        } catch (Exception exc) {
          //Do nothing
        }


      }

      Date currentDate = new Date();
      unregisteredProjects = followingProjects.size() - dateList.size();
      container.put("unregistered", unregisteredProjects);
      for (Date date : dateList) {
        long daysPassed = TimeUnit.DAYS
            .convert(currentDate.getTime() - date.getTime(), TimeUnit.MILLISECONDS);
        if (daysPassed / 7 < 6) {
          container.put("in time", container.get("in time") + 1);
        } else {
          container.put("overdue", container.get("overdue") + 1);
        }
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
