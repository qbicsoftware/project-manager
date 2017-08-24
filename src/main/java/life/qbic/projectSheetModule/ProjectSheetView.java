package life.qbic.projectSheetModule;

import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Panel;

import java.util.Date;

/**
 * Created by sven1103 on 9/01/17.
 */
public interface ProjectSheetView {

    Panel getProjectSheet();

    void setDefaultContent();

    void setProjectCode(String id);

    void showProjectLayout();

    void setRegistrationDate(Date date);

    void setBarcodeSentDate(Date date);

    Button getSaveButton();

    Button getUnfollowButton();

    DateField getRegistrationDateField();

    DateField getBarcodeSentDateField();

}
