package life.qbic.connection.database.userManagementDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import life.qbic.helper.Utils;

public class UserManagementDB {

  String portNumber = "3306";
  String serverName = "portal-testing.am10.uni-tuebingen.de";
  Connection conn = null;

  public UserManagementDB(String userName, String password) {
    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);

    try {
      conn = DriverManager.getConnection(
          "jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/",
          connectionProps);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public int getProjectID(String projectCode) {
    Statement stmt = null;
    int projectID = -1;
    String query = "SELECT id " +
        "FROM " + "qbic_usermanagement_db" + ".projects" +
        " WHERE " + "openbis_project_identifier" + " LIKE " + "'%" + projectCode + "%'";
    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        projectID = rs.getInt("id");
      }
    } catch (Exception e) {
      Utils.notification("Project not found", "The project could not been found in our database",
          "error");
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return projectID;
  }

  public int getPIID(int projectID) {
    Statement stmt = null;
    int personID = -1;
    String query = "SELECT * " +
        "FROM " + "qbic_usermanagement_db" + ".projects_persons" +
        " WHERE " + "project_id" + "=" + projectID + " AND " + "project_role" + " LIKE " + "'%PI%'";
    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        personID = rs.getInt("person_id");
      }
    } catch (SQLException e) {
      //
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return personID;
  }

  public String getPI(int personID) {
    Statement stmt = null;
    String pi = null;
    String query = "SELECT * " +
        "FROM " + "qbic_usermanagement_db" + ".persons" +
        " WHERE " + "id" + "=" + personID;
    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String family = rs.getString("family_name");
        pi = title + " " + first + " " + family;
      }
    } catch (SQLException e) {
      //
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return pi;
  }

  public String getEmail(String personID) {

    Statement stmt = null;
    String email = null;
    String query = "SELECT email " +
        "FROM " + "qbic_usermanagement_db" + ".persons" +
        " WHERE " + "id" + "=" + personID;
    System.out.println(query);
    try {
      stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        email = rs.getString("email");
      }
    } catch (SQLException e) {
      //
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return email;
  }

  public String getProjectPI(String projectCode) {
    String pi = getPI(getPIID(getProjectID(projectCode)));
    System.out.println(pi);
    return pi;
  }


}
