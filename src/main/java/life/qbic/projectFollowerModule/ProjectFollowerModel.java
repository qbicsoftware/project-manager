package life.qbic.projectFollowerModule;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import life.qbic.database.ProjectDatabaseConnector;
import life.qbic.database.QuerryType;
import life.qbic.database.WrongArgumentSettingsException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sven on 12/17/16.
 */
public class ProjectFollowerModel {

    private final ProjectDatabaseConnector projectDatabase;

    private final List<String> allFollingProjects = new ArrayList<>();
    private final HashMap<String, String> querySettings = new HashMap<>();

    private final Map<String, String> spaceProjectMap = new HashMap<>();


    public ProjectFollowerModel(ProjectDatabaseConnector projectDatabase) {
        this.projectDatabase = projectDatabase;
    }


    public List<String> getAllFollowingProjects() {
        return allFollingProjects;
    }


    public ProjectFollowerModel loadFollowingProjects(String sqlTable, String userID, String primaryKey)
            throws SQLException, WrongArgumentSettingsException {
        projectDatabase.connectToDatabase();

        allFollingProjects.clear();

        querySettings.put("table", sqlTable);
        querySettings.put("user_id", userID);

        FreeformQuery query = projectDatabase.makeFreeFormQuery(QuerryType.GET_FOLLOWING_PROJECTS,
                querySettings, primaryKey);


        query.beginTransaction();
        ResultSet followingProjectsQuery = query.getResults(0, 0);
        query.commit();

        followingProjectsQuery.first();

        while (!followingProjectsQuery.isLast()) {
            allFollingProjects.add(followingProjectsQuery.getString("project_id"));
            followingProjectsQuery.next();
        }
        allFollingProjects.add(followingProjectsQuery.getString("project_id"));

        return this;
    }

    void followProject(String sqlTable, String projectCode, String userID, String primaryKey)
            throws SQLException, WrongArgumentSettingsException {
        projectDatabase.connectToDatabase();
        querySettings.put("table", sqlTable);
        querySettings.put("user_id", userID);
        querySettings.put("code", projectCode);

        FreeformQuery query = projectDatabase.makeFreeFormQuery(QuerryType.FOLLOW_PROJECT, querySettings, primaryKey);

        exectuteStatement(query.getQueryString());
    }


    void unfollowProject(String sqlTableName, String selectedProject, String userID, String primaryKey)
            throws SQLException, WrongArgumentSettingsException {
        projectDatabase.connectToDatabase();
        querySettings.put("table", sqlTableName);
        querySettings.put("user_id", userID);
        querySettings.put("code", selectedProject);

        FreeformQuery query = projectDatabase.makeFreeFormQuery(QuerryType.UNFOLLOW_PROJECT, querySettings, primaryKey);

        exectuteStatement(query.getQueryString());
    }

    private void exectuteStatement(String statementString) throws SQLException {
        JDBCConnectionPool pool = projectDatabase.getConnectionPool();
        Connection conn = pool.reserveConnection();
        if (conn != null) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(statementString);
            statement.close();
        } else {
            throw new SQLException("Could not reserve a SQL connection!");
        }
        conn.commit();
        pool.releaseConnection(conn);
    }
}
