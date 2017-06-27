package life.qbic.projectsStatsModule;

import life.qbic.OpenBisConnection;
import life.qbic.database.WrongArgumentSettingsException;
import org.apache.commons.logging.Log;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsPresenter {

    private ProjectsStatsModel model;
    private ProjectsStatsView view;
    private String userID;
    private String primaryKey;
    private String followingProjects, projectsOverview;
    private List<String> projects;
    private Integer overdueProjects;
    private OpenBisConnection connection;
    private final Log log;

    public ProjectsStatsPresenter(ProjectsStatsModel model, ProjectsStatsView view, OpenBisConnection connection, Log log) {
        this.model = model;
        this.view = view;
        this.connection = connection;
        this.log = log;
    }

    public void update() {
        try {
            projects = model.loadFollowingProjects(userID, followingProjects, primaryKey);
        } catch (SQLException e) {
            log.error("Could not load the following/overdue projects.");
        } catch (WrongArgumentSettingsException e) {
            log.error("Could not load the following/overdue projects.");
        }

        try {
            if (projects.size() > 0) {
                overdueProjects = model.getNumberOfOverdueProjects(userID, projectsOverview);
            } else {
                overdueProjects = 0;
            };
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WrongArgumentSettingsException e) {
            e.printStackTrace();
        }
        view.setNumberOfTotalProjects(projects.size());
        view.setNumberOfOverdueProjects(overdueProjects);

    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setFollowingprojects(String followingProjects) {
        this.followingProjects = followingProjects;
    }

    public void setProjectsoverview(String projectsOverview) {
        this.projectsOverview = projectsOverview;
    }
}
