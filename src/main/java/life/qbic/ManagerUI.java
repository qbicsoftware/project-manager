package life.qbic;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import life.qbic.database.projectInvestigatorDB.ProjectDatabase;
import life.qbic.database.projectInvestigatorDB.ProjectDatabaseConnector;
import life.qbic.database.projectInvestigatorDB.ProjectFilter;
import life.qbic.database.projectInvestigatorDB.WrongArgumentSettingsException;
import life.qbic.database.userManagementDB.UserManagementDB;
import life.qbic.openbis.OpenBisConnection;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.overviewChart.OverviewChartPresenter;
import life.qbic.overviewChart.OverviewChartView;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManagerFactory;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import life.qbic.projectFollowerModule.ProjectFollowerModel;
import life.qbic.projectFollowerModule.ProjectFollowerPresenter;
import life.qbic.projectFollowerModule.ProjectFollowerView;
import life.qbic.projectFollowerModule.ProjectFollowerViewImpl;
import life.qbic.projectOverviewModule.ProjectOVPresenter;
import life.qbic.projectOverviewModule.ProjectOverviewModule;
import life.qbic.projectSheetModule.ProjectSheetPresenter;
import life.qbic.projectSheetModule.ProjectSheetView;
import life.qbic.projectSheetModule.ProjectSheetViewImplementation;
import life.qbic.projectsStatsModule.ProjectsStatsModel;
import life.qbic.projectsStatsModule.ProjectsStatsPresenter;
import life.qbic.projectsStatsModule.ProjectsStatsView;
import life.qbic.projectsStatsModule.ProjectsStatsViewImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;


@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.AppWidgetSet")
public class ManagerUI extends UI {


  /**
   * Get static logger instance
   */
  private final static Log log =
      LogFactory.getLog(ManagerUI.class.getName());
  private String userID, url, pw, mysqlUser, mysqlPW;

  @Override
  protected void init(VaadinRequest vaadinRequest) {

    log.info("Started project-manager.");

    getCredentials();

    userID = "zxmqw74";
    //set userID here:
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      userID = LiferayAndVaadinUtils.getUser().getScreenName();
      log.info("UserID = " + userID);
    }

    final VerticalLayout mainFrame = new VerticalLayout();

    final VerticalLayout sliderFrame = new VerticalLayout();

    final VerticalLayout mainContent = new VerticalLayout();

    final ProjectFilter projectFilter = new ProjectFilter();

    final HorizontalLayout statisticsPanel = new HorizontalLayout();

    new ConfigurationManagerFactory();
    final ConfigurationManager config = ConfigurationManagerFactory.getInstance();

    final ProjectDatabaseConnector projectDatabase;
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      projectDatabase = new ProjectDatabase(config.getMysqlUser(), config.getMysqlPass(),
          projectFilter);
    } else {
      projectDatabase = new ProjectDatabase(mysqlUser, mysqlPW, projectFilter);
    }

    final UserManagementDB userManagementDB = new UserManagementDB(mysqlUser, mysqlPW);

    try {
      projectDatabase.connectToDatabase();
      log.info("Connection to SQL project database was successful.");
    } catch (SQLException exp) {
      log.error("Could not connect to SQL project database. Reason: " + exp.getMessage());
    }

    final CssLayout projectDescriptionLayout = new CssLayout();
    final OpenBisClient openBisClient;
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      openBisClient = new OpenBisClient(config.getDataSourceUser(), config.getDataSourcePassword(),
          config.getDataSourceUrl());
    } else {
      openBisClient = new OpenBisClient(userID, pw, url);
    }

    final ProjectFollowerModel followerModel = new ProjectFollowerModel(projectDatabase);

    final ProjectFollowerView followerView = new ProjectFollowerViewImpl()
        .setSpaceCaption("Institution")
        .setProjectCaption("Project")
        .build();

    final OpenBisConnection openBisConnection = new OpenBisConnection();

    if (!openBisConnection.initConnection(openBisClient)) {
      Notification.show("Could not connect to openBis!");
    }

    final ProjectFollowerPresenter followerPresenter = new ProjectFollowerPresenter(followerView,
        followerModel, openBisConnection);
    followerPresenter.setUserID(userID).setSQLTableName("followingprojects").setPrimaryKey("id");

    try {
      followerPresenter.startOrchestration();
    } catch (SQLException | WrongArgumentSettingsException e) {
      e.printStackTrace();
    }

    final ProjectContentModel model = new ProjectContentModel(projectDatabase, userManagementDB,
        followerModel.getAllFollowingProjects(), log, openBisClient);

    final ProjectOverviewModule projectOverviewModule = new ProjectOverviewModule();

    final ProjectOVPresenter projectOVPresenter = new ProjectOVPresenter(model,
        projectOverviewModule, projectDatabase, openBisConnection, log);

    final ProjectSheetView projectSheetView = new ProjectSheetViewImplementation("Project Sheet");

    final ProjectSheetPresenter projectSheetPresenter = new ProjectSheetPresenter(projectSheetView, openBisClient,
        log);

    final OverviewChartView overviewChartView = new OverviewChartView();
    final OverviewChartPresenter overviewChartPresenter = new OverviewChartPresenter(model,
        overviewChartView);

    final ProjectsStatsView projectsStatsView = new ProjectsStatsViewImpl();
    //Init project stats
    final ProjectsStatsModel projectsStatsModel = new ProjectsStatsModel(projectDatabase);
    final ProjectsStatsPresenter projectsStatsPresenter = new ProjectsStatsPresenter(model,
        projectsStatsView);
    projectsStatsPresenter.update();

    //removed pieChartStatusModule #25
    final MasterPresenter masterPresenter = new MasterPresenter(projectOVPresenter,
        projectSheetPresenter, followerPresenter, projectFilter, //timeLineChartPresenter,
        overviewChartPresenter,
        projectsStatsPresenter);

    projectOverviewModule.setWidth(100, Unit.PERCENTAGE);
    projectOverviewModule.addStyleName("overview-module-style");
    projectDescriptionLayout.setSizeFull();
    projectDescriptionLayout.addComponent(projectOverviewModule);
    projectSheetView.getProjectSheet().setSizeUndefined();

    Responsive.makeResponsive(projectDescriptionLayout);
    projectOVPresenter.getUnfollowButton().addClickListener(event -> {
      try {
        String id = projectOVPresenter.getSelectedProject().getValue();
        followerModel.unfollowProject("followingprojects", id, userID, "id");
        followerPresenter.refreshProjects();
        followerPresenter.switchIsChangedFlag();
        log.info("Unfollow: " + id);
        Utils.notification("Unfollow successful", "You unfollowed project " + id, "success");
        projectOverviewModule.getOverviewGrid().deselectAll();
        projectSheetView.setDefaultContent();
      } catch (SQLException | WrongArgumentSettingsException | NullPointerException e) {
        log.error("Unfollowing project failed");
        Utils.notification("Unfollowing project failed", "Please try again later.", "error");
      }
    });

    final SliderPanel sliderPanel = new SliderPanelBuilder(followerView.getUI())
        .caption("FOLLOW PROJECTS")
        .mode(SliderMode.TOP)
        .tabPosition(SliderTabPosition.MIDDLE)
        .style("slider-format")
        .animationDuration(100).zIndex(1).build();
    sliderFrame.addComponent(sliderPanel);

    UI.getCurrent().addClickListener((MouseEvents.ClickListener) event -> {
      if (sliderPanel.isExpanded()) {
        sliderPanel.collapse();
      }
      projectOVPresenter.getSelectedProject().setValue(null);
      projectOVPresenter.clearSelection();
      projectSheetView.setDefaultContent();
      projectSheetView.setProjectCode("");
    });
    sliderPanel.setResponsive(true);
    Responsive.makeResponsive(sliderPanel);
    sliderFrame.setComponentAlignment(sliderPanel, Alignment.MIDDLE_CENTER);
    sliderFrame.setSizeFull();
    sliderFrame.setResponsive(true);
    Responsive.makeResponsive(sliderFrame);
    statisticsPanel.addComponent(overviewChartView);
    statisticsPanel.addComponent(projectsStatsView.getProjectStats());
    statisticsPanel.addComponent(projectSheetView.getProjectSheet());
    statisticsPanel.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

    Responsive.makeResponsive(statisticsPanel);

    projectsStatsPresenter.update();
    //pieChartStatusModule.setSizeUndefined();

    mainContent.addComponent(statisticsPanel);
    mainContent.addComponent(projectDescriptionLayout);
    mainFrame.addComponent(sliderFrame);
    mainFrame.setComponentAlignment(sliderFrame, Alignment.MIDDLE_CENTER);
    mainFrame.addComponent(mainContent);
    mainFrame.setExpandRatio(mainContent, 1);
    mainFrame.setStyleName("mainpage");
    setContent(mainFrame);
  }

  public void getCredentials() {
    Properties prop = new Properties();
    InputStream input = null;

    try {

      input = new FileInputStream("/Users/spaethju/liferay/qbic-ext.properties");

      // load a properties file
      prop.load(input);

      // get the property value and print it out
      url = prop.getProperty("datasource.url");
      pw = prop.getProperty("datasource.password");
      mysqlPW = prop.getProperty("mysql.pass");
      mysqlUser = prop.getProperty("mysql.user");

    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

}