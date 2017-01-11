package life.qbic.portal.database;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by sven on 12/10/16.
 */
public interface ProjectDatabaseConnector {

    boolean connectToDatabase() throws IllegalArgumentException, SQLException;

    SQLContainer loadCompleteTableData(String tableName, String primaryKey) throws SQLException;

    FreeformQuery makeFreeFormQuery(QuerryType type, HashMap arguments, String primaryKey) throws SQLException, WrongArgumentSettingsException;

    JDBCConnectionPool getConnectionPool();


}
