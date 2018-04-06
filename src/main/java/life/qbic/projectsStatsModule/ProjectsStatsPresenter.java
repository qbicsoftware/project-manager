package life.qbic.projectsStatsModule;

import java.util.List;
import life.qbic.openbis.OpenBisConnection;
import life.qbic.ProjectContentModel;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsPresenter {

  private ProjectContentModel model;
  private ProjectsStatsView view;
  private List<String> projects;
  private Integer overdueProjects, unregisteredProjects, intimeProjects;
  private OpenBisConnection connection;

  public ProjectsStatsPresenter(ProjectContentModel model, ProjectsStatsView view) {
    this.model = model;
    this.view = view;
  }

  public void update() {

    projects = model.getFollowingProjects();

    if (projects.size() > 0) {
      overdueProjects = model.getOverdueProjects();
      unregisteredProjects = model.getUnregisteredProjects();
      intimeProjects = model.getInTimeProjects();
    } else {
      overdueProjects = 0;
      intimeProjects = 0;
      overdueProjects = 0;
    }

    view.setNumberOfTotalProjects(projects.size());
    view.setNumberOfOverdueProjects(overdueProjects);
    view.setNumberOfInTimeProjects(intimeProjects);
    view.setNumberOfUnregisteredProjects(unregisteredProjects);
  }
}
