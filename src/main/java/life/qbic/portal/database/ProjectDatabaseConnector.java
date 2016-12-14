package life.qbic.portal.database;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;

import java.sql.SQLException;

/**
 * Created by sven on 12/10/16.
 */
public interface ProjectDatabaseConnector {

    void connectToDatabase() throws IllegalArgumentException, SQLException;

    SQLContainer loadCompleteTableData() throws SQLException;

    FreeformQuery makeFreeFormQuery(QuerryType type) throws SQLException;


}