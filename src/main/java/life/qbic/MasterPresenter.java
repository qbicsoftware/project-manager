package life.qbic;

import com.vaadin.data.Property;
import life.qbic.connection.database.projectInvestigatorDB.ProjectFilter;
import life.qbic.module.overviewChartModule.OverviewChartPresenter;
import life.qbic.module.projectFollowerModule.ProjectFollowerPresenter;
import life.qbic.module.projectOverviewModule.ProjectOVPresenter;
import life.qbic.module.projectSheetModule.ProjectSheetPresenter;
import life.qbic.module.projectsStatsModule.ProjectsStatsPresenter;
import life.qbic.module.timelineChartModule.TimelineChartPresenter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a master presenter class and helps to communicate between the different modules
 */
public class MasterPresenter {

  //private final PieChartStatusModule pieChartStatusModule;

  private final static Log log =
      LogFactory.getLog(ManagerUI.class.getName());
  private final ProjectOVPresenter projectOverviewPresenter;
  private final ProjectSheetPresenter projectSheetPresenter;
  private final ProjectFollowerPresenter projectFollowerPresenter;

  //private final TimeLineChartPresenter timeLineChartPresenter;
  private final ProjectFilter projectFilter;
  private final OverviewChartPresenter overviewChartPresenter;
  private final ProjectsStatsPresenter projectsStatsPresenter;
  private final TimelineChartPresenter timelineChartPresenter;

  //removed PieChartStatusModule pieChartStatusModule #25
  MasterPresenter(ProjectOVPresenter projectOverviewPresenter,
      ProjectSheetPresenter projectSheetPresenter,
      ProjectFollowerPresenter projectFollowerPresenter,
      ProjectFilter projectFilter,
      //TimeLineChartPresenter timeLineChartPresenter,
      OverviewChartPresenter overviewChartPresenter,
      ProjectsStatsPresenter projectsStatsPresenter,
      TimelineChartPresenter timelineChartPresenter) {
    //this.pieChartStatusModule = pieChartStatusModule;
    this.projectOverviewPresenter = projectOverviewPresenter;
    this.projectFollowerPresenter = projectFollowerPresenter;
    this.projectSheetPresenter = projectSheetPresenter;
    this.projectFilter = projectFilter;
    //this.timeLineChartPresenter = timeLineChartPresenter;
    this.overviewChartPresenter = overviewChartPresenter;
    this.projectsStatsPresenter = projectsStatsPresenter;
    this.timelineChartPresenter = timelineChartPresenter;

    init();
  }

  private void init() {
    makeFilter();

    try {
      projectOverviewPresenter.init();
      log.info("Init projectoverview module successfully.");
    } catch (Exception exp) {
      log.fatal("Init of projectoverview module failed. Reason: " + exp.getMessage(), exp);
      projectOverviewPresenter.sendError("Project Overview Module failed.", exp.getMessage());
    }

    //projectOverviewPresenter.getStatusKeyFigures().forEach(pieChartStatusModule::update);

    projectOverviewPresenter.getSelectedProject().addValueChangeListener(event -> {
      projectSheetPresenter.init();
      projectSheetPresenter
          .showInfoForProject(projectOverviewPresenter.getSelectedProjectItem());
    });

    projectOverviewPresenter.getIsChangedFlag().addValueChangeListener(this::refreshModuleViews);

    projectSheetPresenter.getInformationCommittedFlag()
        .addValueChangeListener(this::refreshModuleViews);
    projectFilter.createFilter("projectID", projectFollowerPresenter.getFollowingProjects());
    projectFollowerPresenter.getIsChangedFlag().addValueChangeListener(event -> {
      final String selectedProject = projectFollowerPresenter.getCurrentProject();
      boolean doesDBEntryExist = projectOverviewPresenter
          .isProjectInFollowingTable(selectedProject);
      if (!doesDBEntryExist) {
        projectOverviewPresenter.createNewProjectEntry(selectedProject);
      }
      refreshModuleViews(event);
    });

    if (projectFollowerPresenter.getFollowingProjects().size() > 0) {
      //timeLineChartPresenter.setCategories(projectOverviewPresenter.getTimeLineStats());
      makeFilter();
      overviewChartPresenter.update();
      timelineChartPresenter.update();
    }

  }

  private void refreshModuleViews(Property.ValueChangeEvent event) {
    makeFilter();
    projectOverviewPresenter.refreshView();
    //projectOverviewPresenter.getStatusKeyFigures().forEach(pieChartStatusModule::update);
    //timeLineChartPresenter.updateData(projectOverviewPresenter.getTimeLineStats());
    overviewChartPresenter.update();
    timelineChartPresenter.update();
    projectsStatsPresenter.update();
    if (projectFollowerPresenter.getFollowingProjects().size() == 0) {
      projectSheetPresenter.getProjectSheetView().getProjectSheet().setVisible(false);
    } else {
      projectSheetPresenter.getProjectSheetView().getProjectSheet().setVisible(true);
    }
  }

  private void makeFilter() {
    projectFilter.createFilter("projectID", projectFollowerPresenter.getFollowingProjects());
  }
}
