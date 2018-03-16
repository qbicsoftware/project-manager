package life.qbic.projectSheetModule;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import java.util.Date;

/**
 * Created by sven1103 on 9/01/17.
 */
public interface ProjectSheetView {

    VerticalLayout getProjectSheet();

    void setDefaultContent();

    void setProjectCode(String id);

    void showProjectLayout();


}
