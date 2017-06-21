package life.qbic.projectOverviewModule;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

import java.util.ArrayList;

/**
 * Created by sven1103 on 28/11/16.
 */
public class ColumnFieldTypes {

    static ComboBox PROJECTSTATUS = new ComboBox();

    static {
        PROJECTSTATUS.addItem("open");
        PROJECTSTATUS.addItem("in progress");
        PROJECTSTATUS.addItem("closed");
    }

    static ComboBox PROJECTREGISTERED = new ComboBox();

    static {
        PROJECTREGISTERED.addItem("no");
        PROJECTREGISTERED.addItem("in progress");
        PROJECTREGISTERED.addItem("done");
    }

    static ComboBox BARCODESENT = new ComboBox();

    static {
        BARCODESENT.addItem("no");
        BARCODESENT.addItem("in progress");
        BARCODESENT.addItem("done");
    }

    static ComboBox DATAPROCESSED = new ComboBox();

    static {
        DATAPROCESSED.addItem("no");
        DATAPROCESSED.addItem("in progress");
        DATAPROCESSED.addItem("done");
    }

    static ComboBox DATAANALYZED = new ComboBox();

    static {
        DATAANALYZED.addItem("no");
        DATAANALYZED.addItem("in progress");
        DATAANALYZED.addItem("done");
    }


    static ComboBox REPORTSENT = new ComboBox();

    static {
        REPORTSENT.addItem("no");
        REPORTSENT.addItem("in progress");
        REPORTSENT.addItem("done");
    }

    static DateField RAWDATAREGISTERED = new DateField();

    static {
        RAWDATAREGISTERED.setDateFormat("yyyy-MM-dd");
    }

    static ArrayList<Field> fields = new ArrayList<>();


    static {
        fields.add(PROJECTSTATUS);
        fields.add(PROJECTREGISTERED);
        fields.add(BARCODESENT);
        fields.add(DATAPROCESSED);
        fields.add(DATAANALYZED);
        fields.add(REPORTSENT);
    }

    public static void clearFromParents() {
        fields.forEach((Field field) ->
                field.setParent(null));
    }

}
