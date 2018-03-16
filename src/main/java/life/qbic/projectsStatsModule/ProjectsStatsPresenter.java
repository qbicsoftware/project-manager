package life.qbic.projectsStatsModule;

import life.qbic.OpenBisConnection;
import life.qbic.ProjectContentModel;

import java.util.List;

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
