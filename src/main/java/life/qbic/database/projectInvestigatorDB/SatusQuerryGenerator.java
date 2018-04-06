package life.qbic.database.projectInvestigatorDB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sven on 12/11/16.
 */
public class SatusQuerryGenerator {

  private static final Map<QuerryType, String> querryMap = new HashMap<>();

  private static final Map<QuerryType, String> followingProjectMap = new HashMap<>();

  static {
    querryMap.put(QuerryType.PROJECTSTATUS_OPEN, "SELECT * FROM %s WHERE projectStatus=\'open\'");
    querryMap.put(QuerryType.PROJECTSTATUS_INPROGRESS,
        "SELECT * FROM %s WHERE projectStatus=\'in progress\'");
    querryMap
        .put(QuerryType.PROJECTSTATUS_CLOSED, "SELECT * FROM %s WHERE projectStatus=\'closed\'");
    querryMap.put(QuerryType.PROJECT_REGISTERED_RAWDATA,
        "SELECT * FROM %s WHERE rawDataRegistered < NOW() - INTERVAL 12 WEEK");
    followingProjectMap
        .put(QuerryType.GET_FOLLOWING_PROJECTS, "SELECT * FROM %s WHERE user_id=\'%s\'");
    followingProjectMap.put(QuerryType.FOLLOW_PROJECT,
        "INSERT INTO %s(user_id, project_id) VALUES (\'%s\', \'%s\');");
    followingProjectMap.put(QuerryType.UNFOLLOW_PROJECT,
        "DELETE FROM %s WHERE user_id=\'%s\' AND project_id=\'%s\' ;");
  }

  public static String getQuerryFromType(QuerryType type, HashMap arguments,
      List<String> followingProjects) throws WrongArgumentSettingsException {
    if (arguments == null || arguments.isEmpty()) {
      throw new WrongArgumentSettingsException("The argument map is empty ord not given");
    }
    if (querryMap.containsKey(type) && arguments.containsKey("table")) {
      return (String.format(querryMap.get(type), arguments.get("table")) + getTheOrWhereFilterQuery(
          followingProjects));
    }
    return getFollowingProjects(type, arguments);
  }

  private static String getFollowingProjects(QuerryType type, HashMap arguments)
      throws WrongArgumentSettingsException {

    if (arguments.containsKey("table") && arguments.containsKey("user_id")) {

      if (followingProjectMap.containsKey(type) && arguments.containsKey("code")) {
        return String
            .format(followingProjectMap.get(type), arguments.get("table"), arguments.get("user_id"),
                arguments.get("code"));
      } else if (!type.equals(QuerryType.GET_FOLLOWING_PROJECTS)) {
        throw new WrongArgumentSettingsException(
            "You must provide the project code for this type of query.");
      }
      return String
          .format(followingProjectMap.get(type), arguments.get("table"), arguments.get("user_id"));
    }
    throw new WrongArgumentSettingsException("Table or user_id argument is missing");
  }

  private static String getTheOrWhereFilterQuery(List<String> followingProjects) {
    StringBuilder query = new StringBuilder();
    query.append(" AND (");
    for (String filter : followingProjects.subList(0, followingProjects.size() - 1)) {
      query.append(String.format(" projectID = \'%s\'", filter));
      query.append(" OR");
    }
    query.append(String
        .format(" projectID = \'%s\');", followingProjects.get(followingProjects.size() - 1)));
    return query.toString();
  }

}
