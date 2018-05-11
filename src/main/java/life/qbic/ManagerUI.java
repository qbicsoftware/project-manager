package life.qbic;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import life.qbic.connection.database.projectInvestigatorDB.ProjectDatabase;
import life.qbic.connection.database.projectInvestigatorDB.ProjectDatabaseConnector;
import life.qbic.connection.database.projectInvestigatorDB.ProjectFilter;
import life.qbic.connection.database.projectInvestigatorDB.WrongArgumentSettingsException;
import life.qbic.connection.database.userManagementDB.UserManagementDB;
import life.qbic.connection.openbis.OpenBisConnection;
import life.qbic.helper.Utils;
import life.qbic.module.overviewChartModule.OverviewChartPresenter;
import life.qbic.module.overviewChartModule.OverviewChartView;
import life.qbic.module.projectFollowerModule.ProjectFollowerModel;
import life.qbic.module.projectFollowerModule.ProjectFollowerPresenter;
import life.qbic.module.projectFollowerModule.ProjectFollowerView;
import life.qbic.module.projectFollowerModule.ProjectFollowerViewImpl;
import life.qbic.module.projectOverviewModule.ProjectOVPresenter;
import life.qbic.module.projectOverviewModule.ProjectOverviewModule;
import life.qbic.module.projectSheetModule.ProjectSheetPresenter;
import life.qbic.module.projectSheetModule.ProjectSheetView;
import life.qbic.module.projectSheetModule.ProjectSheetViewImplementation;
import life.qbic.module.projectsStatsModule.ProjectsStatsModel;
import life.qbic.module.projectsStatsModule.ProjectsStatsPresenter;
import life.qbic.module.projectsStatsModule.ProjectsStatsView;
import life.qbic.module.projectsStatsModule.ProjectsStatsViewImpl;
import life.qbic.module.timelineChartModule.TimelineChartPresenter;
import life.qbic.module.timelineChartModule.TimelineChartView;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManagerFactory;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
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
  private String userID, pw, mysqlUser, mysqlPW;

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

    // Connect to openbis
    IDataStoreServerApi dss =
        HttpInvokerUtils.createStreamSupportingServiceStub(IDataStoreServerApi.class,
            "https://qbis.qbic.uni-tuebingen.de:444/datastore_server"
                + IDataStoreServerApi.SERVICE_URL, 10000);

    // get a reference to AS API
    IApplicationServerApi app = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
        "https://qbis.qbic.uni-tuebingen.de/openbis/openbis" + IApplicationServerApi.SERVICE_URL,
        10000);

    String sessionToken = "";

    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      // login to obtain a session token
      sessionToken = app.login(config.getDataSourceUser(), config.getDataSourcePassword());
    } else {
      sessionToken = app.login(userID, pw);
    }

    OpenBisConnection openBisConnection = new OpenBisConnection(app, dss, sessionToken);
    openBisConnection.getSpaceOfProject("QGTSG");

    final ProjectFollowerModel followerModel = new ProjectFollowerModel(projectDatabase);

    final ProjectFollowerView followerView = new ProjectFollowerViewImpl()
        .setSpaceCaption("Institution")
        .setProjectCaption("Project")
        .build();

    final ProjectFollowerPresenter followerPresenter = new ProjectFollowerPresenter(followerView,
        followerModel, openBisConnection);
    followerPresenter.setUserID(userID).setSQLTableName("followingprojects").setPrimaryKey("id");

    try {
      followerPresenter.startOrchestration();
    } catch (SQLException | WrongArgumentSettingsException e) {
      e.printStackTrace();
    }

    final ProjectContentModel model = new ProjectContentModel(projectDatabase, userManagementDB,
        followerModel.getAllFollowingProjects(), log, openBisConnection);

    final ProjectOverviewModule projectOverviewModule = new ProjectOverviewModule();

    final OverviewChartView overviewChartView = new OverviewChartView();
    final OverviewChartPresenter overviewChartPresenter = new OverviewChartPresenter(model,
        overviewChartView);

    final TimelineChartView timelineChartView = new TimelineChartView();
    final TimelineChartPresenter timelineChartPresenter = new TimelineChartPresenter(model,
        timelineChartView);

    final ProjectOVPresenter projectOVPresenter = new ProjectOVPresenter(model,
        projectOverviewModule, overviewChartPresenter, openBisConnection, projectDatabase, log);

    final ProjectSheetView projectSheetView = new ProjectSheetViewImplementation();

    final ProjectSheetPresenter projectSheetPresenter = new ProjectSheetPresenter(projectSheetView, log);

    final ProjectsStatsView projectsStatsView = new ProjectsStatsViewImpl();
    //Init project stats
    final ProjectsStatsModel projectsStatsModel = new ProjectsStatsModel(projectDatabase);
    final ProjectsStatsPresenter projectsStatsPresenter = new ProjectsStatsPresenter(model,
        projectsStatsView);
    projectsStatsPresenter.update();

    final MasterPresenter masterPresenter = new MasterPresenter(projectOVPresenter,
        projectSheetPresenter, followerPresenter, projectFilter, //timeLineChartPresenter,
        overviewChartPresenter,
        projectsStatsPresenter,
        timelineChartPresenter);

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
        projectSheetView.reset();
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
      projectSheetPresenter.init();
      projectOVPresenter.clearSelection();
      projectSheetView.reset();
    });
    sliderPanel.setResponsive(true);
    Responsive.makeResponsive(sliderPanel);
    sliderFrame.setComponentAlignment(sliderPanel, Alignment.MIDDLE_CENTER);
    sliderFrame.setSizeFull();
    sliderFrame.setResponsive(true);
    Responsive.makeResponsive(sliderFrame);
    VerticalLayout statsLayout = new VerticalLayout();
    statsLayout.addComponents(overviewChartView, projectsStatsView.getStatsLayout());
    statsLayout.setSizeFull();
    statsLayout.setComponentAlignment(projectsStatsView.getStatsLayout(), Alignment.MIDDLE_CENTER);
    statisticsPanel.addComponent(timelineChartView);
    statisticsPanel.addComponent(statsLayout);
    //statisticsPanel.addComponent(projectSheetView.getProjectSheet());
    statisticsPanel.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    statisticsPanel.setSizeFull();
    statisticsPanel.setMargin(new MarginInfo(false, true, false, false));
    statisticsPanel.setSpacing(false);

    Responsive.makeResponsive(statisticsPanel);

    projectsStatsPresenter.update();

    mainContent.addComponent(statisticsPanel);
    mainContent.addComponent(projectDescriptionLayout);
    mainContent.setSpacing(true);
    mainFrame.setSpacing(true);
    mainFrame.addComponent(sliderFrame);
    mainFrame.setComponentAlignment(sliderFrame, Alignment.MIDDLE_CENTER);
    mainFrame.addComponent(mainContent);
    mainFrame.setExpandRatio(mainContent, 1);
    mainFrame.setStyleName("mainpage");
    setContent(mainFrame);
  }

  private void getCredentials() {
    Properties prop = new Properties();
    InputStream input = null;

    try {

      input = new FileInputStream("/Users/spaethju/liferay/qbic-ext.properties");

      // load a properties file
      prop.load(input);

      // get the property value and print it out
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