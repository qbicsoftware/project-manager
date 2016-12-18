package life.qbic.portal.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sven on 12/11/16.
 */
public class SatusQuerryGenerator {

    private static final Map <QuerryType, String> querryMap = new HashMap<>();

    private static final Map <QuerryType, String> followingProjectMap = new HashMap<>();

    static{
        querryMap.put(QuerryType.PROJECTSTATUS_OPEN, "SELECT * FROM %s WHERE projectStatus=\'open\'");
        querryMap.put(QuerryType.PROJECTSTATUS_INPROGRESS, "SELECT * FROM %s WHERE projectStatus=\'in progress\'");
        querryMap.put(QuerryType.PROJECTSTATUS_CLOSED, "SELECT * FROM %s WHERE projectStatus=\'closed\'");
        followingProjectMap.put(QuerryType.GET_FOLLOWING_PROJECTS, "SELECT * FROM %s WHERE user_id=\'%s\'");
    }

    public static String getQuerryFromType(QuerryType type, HashMap arguments) throws WrongArgumentSettingsException{
        if (arguments == null || arguments.isEmpty()){
            throw new WrongArgumentSettingsException("The argument map is empty ord not given");
        }
        if (querryMap.containsKey(type) && arguments.containsKey("table")) {
            return String.format(querryMap.get(type), arguments.get("table"));
        }
        return getFollowingProjects(type, arguments);
    }

    private static String getFollowingProjects(QuerryType type, HashMap arguments) throws WrongArgumentSettingsException{
        if (arguments.containsKey("table") && arguments.containsKey("user_id")) {
            return String.format(followingProjectMap.get(type), arguments.get("table"), arguments.get("user_id"));
        }
        throw new WrongArgumentSettingsException("Table or user_id argument is missing");
    }

}
