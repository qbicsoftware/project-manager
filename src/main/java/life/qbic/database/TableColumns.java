package life.qbic.database;

import java.util.HashMap;

/**
 * This class contains maps for the different SQL table columns as ENUM and their
 * real String representation in the database.
 * It provides an easy to maintained integration of the columns names, in case of changes
 * in the database, simply adjust the maps here.
 */
public class TableColumns {

    public final static HashMap<ColumnTypes, String> PROJECTOVERVIEWTABLE = new HashMap<>();

    static {
        PROJECTOVERVIEWTABLE.put(ColumnTypes.ID, "id");
        PROJECTOVERVIEWTABLE.put(ColumnTypes.PROJECTID, "projectID");
        PROJECTOVERVIEWTABLE.put(ColumnTypes.PROJECTREGISTERED, "projectRegistered");
        PROJECTOVERVIEWTABLE.put(ColumnTypes.REGISTRATIONDATE, "projectRegisteredDate");
        PROJECTOVERVIEWTABLE.put(ColumnTypes.BARCODESENTDATE, "barcodeSentDate");
        PROJECTOVERVIEWTABLE.put(ColumnTypes.RAWDATAREGISTERED, "rawDataRegistered");
    }


}
