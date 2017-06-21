package life.qbic.projectFollowerModule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import life.qbic.OpenBisConnection;
import life.qbic.beans.ProjectBean;
import life.qbic.database.WrongArgumentSettingsException;
import org.vaadin.teemu.switchui.Switch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sven on 12/18/16.
 */
public class ProjectFollowerPresenter {

    private final ProjectFollowerView view;
    private final ProjectFollowerModel model;
    private final OpenBisConnection connection;
    private String sqlTableName;
    private String userID;
    private String primaryKey;
    private Map<String, List<String>> spaceProjectMap;
    private List<String> followingProjects;
    private String currentProject;
    private ObjectProperty<Boolean> isChangedFlag;

    public ProjectFollowerPresenter(ProjectFollowerView view, ProjectFollowerModel model,
                                    OpenBisConnection openBisConnection) {
        this.view = view;
        this.model = model;
        this.connection = openBisConnection;
        this.isChangedFlag = new ObjectProperty<Boolean>(false);
    }


    public void startOrchestration() throws NullPointerException, SQLException, WrongArgumentSettingsException {

        BeanItemContainer<ProjectBean> projectBeanBeanItemContainer = connection.getListOfProjects();

        followingProjects = model.loadFollowingProjects(sqlTableName, userID, primaryKey).getAllFollowingProjects();

        updateSpaceProjectMap(projectBeanBeanItemContainer);

        view.getSpaceComboBox().addItems(spaceProjectMap.keySet());
        view.getProjectComboBox().addItems(getProjectsFromSpace(""));

         /*
        Listener for the space combo box
         */
        view.getSpaceComboBox().addValueChangeListener(valueChangeEvent -> {
            String selectedSpace = (String) valueChangeEvent.getProperty().getValue();
            updateProjectBox(selectedSpace);
        });

        /*
        Listener for the project combo box
         */
        view.getProjectComboBox().addValueChangeListener(valueChangeEvent -> {
            String selectedProject = (String) valueChangeEvent.getProperty().getValue();

            this.view.getDescriptionField().setValue(
                    shortenDescription(connection.getProjectDescription(selectedProject)));

            Switch followerSwitch = view.getFollowSwitch();
            if (selectedProject == null) {
                followerSwitch.setValue(false);
                followerSwitch.setEnabled(false);
                return;
            }
            followerSwitch.setEnabled(true);
            if (followingProjects.contains(selectedProject)) {
                followerSwitch.setValue(true);
            } else {
                followerSwitch.setValue(false);
            }
            currentProject = selectedProject;
        });

        /*
        Listener for the follower SWITCH
         */
        view.getFollowSwitch().addValueChangeListener(focusEvent -> {
            String selectedProject = (String) view.getProjectComboBox().getValue();
            if (selectedProject == null)
                selectedProject = "";
            if (view.getFollowSwitch().getValue() && selectedProject.equals(currentProject)) {
                try {
                    model.followProject(sqlTableName, selectedProject, userID, primaryKey);
                    refreshProjects();
                    switchIsChangedFlag();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            } else if (!view.getFollowSwitch().getValue() && selectedProject.equals(currentProject)) {
                try {
                    model.unfollowProject(sqlTableName, selectedProject, userID, primaryKey);
                    refreshProjects();
                    switchIsChangedFlag();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }

        });

    }

    private String shortenDescription(String projectDescription) {
        if (projectDescription.length() > 100) {
            return projectDescription.substring(0, 100) + " ...";
        } else return projectDescription;
    }

    public ProjectFollowerPresenter setSQLTableName(String tableName) {
        this.sqlTableName = tableName;
        return this;
    }

    public ProjectFollowerPresenter setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public ProjectFollowerPresenter setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }


    private void updateSpaceProjectMap(BeanItemContainer<ProjectBean> projectBeanBeanItemContainer) {
        spaceProjectMap = new HashMap<>();

        projectBeanBeanItemContainer.getItemIds().forEach((ProjectBean project) -> {

            String space = project.getSpace();
            String code = project.getCode();
            try {
                spaceProjectMap.get(space).add(code);
            } catch (NullPointerException exp) {
                spaceProjectMap.put(space, new ArrayList<>());
                spaceProjectMap.get(space).add(code);
            }
        });
    }

    private List<String> getProjectsFromSpace(String space) {
        if (space == null || space.equalsIgnoreCase("")) {
            List<String> allProjects = new ArrayList<>();
            spaceProjectMap.values().forEach(project -> allProjects.addAll(project));
            return allProjects;
        } else {
            return this.spaceProjectMap.get(space);
        }
    }

    private void updateProjectBox(String space) {
        view.getProjectComboBox().removeAllItems();
        view.getProjectComboBox().addItems(getProjectsFromSpace(space));
    }

    private void refreshProjects() throws SQLException, WrongArgumentSettingsException {
        this.followingProjects = model.loadFollowingProjects(sqlTableName, userID, primaryKey).getAllFollowingProjects();
    }

    private void switchIsChangedFlag() {
        System.out.println("Following change event fired!");
        this.isChangedFlag.setValue(!this.isChangedFlag.getValue());
    }

    public ObjectProperty<Boolean> getIsChangedFlag() {
        return this.isChangedFlag;
    }

    public List<String> getFollowingProjects() {
        try {
            refreshProjects();
            followingProjects.forEach(System.out::println);
        } catch (Exception exp) {
            System.err.println("Refreshing failed.");
        }
        return this.followingProjects;
    }

    public String getCurrentProject() {
        return this.currentProject;
    }


}
