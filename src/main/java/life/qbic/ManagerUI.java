package life.qbic;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import life.qbic.database.ProjectDatabase;
import life.qbic.database.ProjectDatabaseConnector;
import life.qbic.database.ProjectFilter;
import life.qbic.database.WrongArgumentSettingsException;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManager;
import life.qbic.portal.liferayandvaadinhelpers.main.ConfigurationManagerFactory;
import life.qbic.portal.liferayandvaadinhelpers.main.LiferayAndVaadinUtils;
import life.qbic.projectFollowerModule.ProjectFollowerModel;
import life.qbic.projectFollowerModule.ProjectFollowerPresenter;
import life.qbic.projectFollowerModule.ProjectFollowerView;
import life.qbic.projectFollowerModule.ProjectFollowerViewImpl;
import life.qbic.projectOverviewModule.ProjectContentModel;
import life.qbic.projectOverviewModule.ProjectOVPresenter;
import life.qbic.projectOverviewModule.ProjectOverviewModule;
import life.qbic.projectSheetModule.ProjectSheetPresenter;
import life.qbic.projectSheetModule.ProjectSheetView;
import life.qbic.projectSheetModule.ProjectSheetViewImplementation;
import life.qbic.projectsStatsModule.ProjectsStatsModel;
import life.qbic.projectsStatsModule.ProjectsStatsPresenter;
import life.qbic.projectsStatsModule.ProjectsStatsView;
import life.qbic.projectsStatsModule.ProjectsStatsViewImpl;
import life.qbic.projectsTimeLineChart.TimeLineChart;
import life.qbic.projectsTimeLineChart.TimeLineChartPresenter;
import life.qbic.projectsTimeLineChart.TimeLineModel;
import life.qbic.projectsTimeLineChart.TimeLineStats;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.WrappedPortletSession;


@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.AppWidgetSet")
public class ManagerUI extends UI {


    private String userID;
    private Properties properties;
    /**
     * Get static logger instance
     */
    private final static Log log =
            LogFactory.getLog(ManagerUI.class.getName());

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        log.info("Started project-manager.");

        //set userID here:
        if (LiferayAndVaadinUtils.isLiferayPortlet()) {
           userID = LiferayAndVaadinUtils.getUser().getScreenName();
           log.info("UserID = " + userID);
        }


        final VerticalLayout mainFrame = new VerticalLayout();

        final VerticalLayout sliderFrame = new VerticalLayout();

        final VerticalLayout mainContent = new VerticalLayout();

        final ProjectFilter projectFilter = new ProjectFilter();

        final CssLayout statisticsPanel = new CssLayout();

        ConfigurationManager config = new ConfigurationManagerFactory().getInstance();

        final ProjectDatabaseConnector projectDatabase = new ProjectDatabase(config.getMysqlUser(), config.getMysqlPass(), projectFilter);

        try {
            projectDatabase.connectToDatabase();
            log.info("Connection to SQL project database was successful.");
        } catch (SQLException exp) {
            log.error("Could not connect to SQL project database. Reason: " + exp.getMessage());
        }


        final CssLayout projectDescriptionLayout = new CssLayout();
        final OpenBisClient openBisClient = new OpenBisClient(config.getDataSourceUser(),
                config.getDataSourcePassword(), config.getDataSourceUrl());


        final ProjectFollowerModel followerModel = new ProjectFollowerModel(projectDatabase);

        final ProjectFollowerView followerView = new ProjectFollowerViewImpl()
                .setSpaceCaption("Institution")
                .setProjectCaption("Project")
                .build();

        final OpenBisConnection openBisConnection = new OpenBisConnection();

        if (!openBisConnection.initConnection(openBisClient)) {
            Notification.show("Could not connect to openBis!");
        }

        final ProjectFollowerPresenter followerPresenter = new ProjectFollowerPresenter(followerView, followerModel, openBisConnection);
        followerPresenter.setUserID(userID).setSQLTableName("followingprojects").setPrimaryKey("id");

        try {
            followerPresenter.startOrchestration();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WrongArgumentSettingsException e) {
            e.printStackTrace();
        }


        final ProjectContentModel model = new ProjectContentModel(projectDatabase, followerModel.getAllFollowingProjects(), log);

        //final PieChartStatusModule pieChartStatusModule = new PieChartStatusModule();

        final ProjectOverviewModule projectOverviewModule = new ProjectOverviewModule();

        final ProjectOVPresenter projectOVPresenter = new ProjectOVPresenter(model,
                projectOverviewModule, projectDatabase, openBisConnection, log);


        final ProjectSheetView projectSheetView = new ProjectSheetViewImplementation("Project Sheet");

        final ProjectSheetPresenter projectSheetPresenter = new ProjectSheetPresenter(projectSheetView, projectDatabase, log);

        final TimeLineChart timeLineChart = new TimeLineChart();

        timeLineChart.setTitle("Time since raw data arrived");

        final TimeLineStats timeLineModel = new TimeLineModel();

        final TimeLineChartPresenter timeLineChartPresenter = new TimeLineChartPresenter(timeLineModel, timeLineChart);

        final ProjectsStatsView projectsStatsView = new ProjectsStatsViewImpl();
        //Init project stats
        final ProjectsStatsModel projectsStatsModel = new ProjectsStatsModel(projectDatabase);
        final ProjectsStatsPresenter projectsStatsPresenter = new ProjectsStatsPresenter(projectsStatsModel, projectsStatsView, openBisConnection, log);
        projectsStatsPresenter.setUserID(userID);
        projectsStatsPresenter.setFollowingprojects("followingprojects");
        projectsStatsPresenter.setProjectsoverview("projectsoverview");
        projectsStatsPresenter.setPrimaryKey("id");
        projectsStatsPresenter.update();

        //removed pieChartStatusModule #25
        final MasterPresenter masterPresenter = new MasterPresenter(projectOVPresenter, projectSheetPresenter, followerPresenter, projectFilter, timeLineChartPresenter, projectsStatsPresenter);

        projectOverviewModule.setWidth(100, Unit.PERCENTAGE);
        projectOverviewModule.addStyleName("overview-module-style");
        projectDescriptionLayout.setSizeFull();
        projectDescriptionLayout.addComponent(projectOverviewModule);
        projectDescriptionLayout.addComponent(projectSheetView.getProjectSheet());
        projectSheetView.getProjectSheet().setSizeUndefined();

        Responsive.makeResponsive(projectDescriptionLayout);


        final SliderPanel sliderPanel = new SliderPanelBuilder(followerView.getUI())
                .caption("FOLLOW PROJECTS")
                .mode(SliderMode.TOP)
                .tabPosition(SliderTabPosition.MIDDLE)
                .style("slider-format")
                .animationDuration(100).build();
        sliderFrame.addComponent(sliderPanel);
        sliderFrame.setComponentAlignment(sliderPanel, Alignment.MIDDLE_CENTER);
        sliderFrame.setWidth("50%");
        //statisticsPanel.addComponent(pieChartStatusModule);
        //pieChartStatusModule.setStyleName("statsmodule");
        timeLineChart.setStyleName("statsmodule");
        statisticsPanel.addComponent(timeLineChart);
        statisticsPanel.setWidth(100, Unit.PERCENTAGE);
        statisticsPanel.addComponent(projectsStatsView.getProjectStats());

        Responsive.makeResponsive(statisticsPanel);

        timeLineChart.setSizeUndefined();
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

    private String getPortletContextName(VaadinRequest request) {
        WrappedPortletSession wrappedPortletSession = (WrappedPortletSession) request
                .getWrappedSession();
        PortletSession portletSession = wrappedPortletSession
                .getPortletSession();

        final PortletContext context = portletSession.getPortletContext();
        final String portletContextName = context.getPortletContextName();
        return portletContextName;
    }

    private Integer getPortalCountOfRegisteredUsers() {
        Integer result = null;

        try {
            result = UserLocalServiceUtil.getUsersCount();
        } catch (SystemException e) {
            log.error(e);
        }

        return result;
    }

    private Map<String, String> getCredentialsFromEnvVariables() {
        final Map<String, String> credentials = new HashMap<>();
        credentials.put("sqluser", properties.getProperty("mysql.user"));
        credentials.put("sqlpassword", properties.getProperty("mysql.pass"));

        if (credentials.get("sqluser") != null && credentials.get("sqlpassword") != null) {
            return credentials;
        } else {
            return null;
        }
    }

    private Properties getPropertiesFromFile(String path) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Paths.get(path)));
        } catch (Exception exp) {
            System.err.println(("Could not open or read from properties file!"));
        }

        return properties;
    }
}