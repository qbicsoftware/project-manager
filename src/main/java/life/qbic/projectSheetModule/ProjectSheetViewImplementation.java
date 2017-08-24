package life.qbic.projectSheetModule;


import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;

/**
 * Created by sven1103 on 9/01/17.
 */
public class ProjectSheetViewImplementation implements ProjectSheetView {

    private Panel projectSheet;

    private String projectCode;

    private Label projectCodeLabel;

    private Date registrationDate;

    private DateField registrationDateField;

    private Button saveButton, unfollowButton;

    private DateField barcodeSendDateField;


    public ProjectSheetViewImplementation() {
        this.projectSheet = new Panel();
        init();
    }

    public ProjectSheetViewImplementation(String caption) {
        this.projectSheet = new Panel(caption);
        init();
    }

    private void init() {
        setDefaultContent();
        projectCode = "";
        projectCodeLabel = new Label();
        projectCodeLabel.setIcon(FontAwesome.INFO_CIRCLE);

        registrationDateField = new PopupDateField();
        barcodeSendDateField = new PopupDateField();
        barcodeSendDateField.setDateFormat("yyyy-MM-dd");
        registrationDateField.setDateFormat("yyyy-MM-dd");

        saveButton = new Button("Save");
        saveButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        saveButton.addStyleName(ValoTheme.BUTTON_SMALL);
        unfollowButton = new Button("Unfollow");
        unfollowButton.setStyleName(ValoTheme.BUTTON_DANGER);
        unfollowButton.addStyleName(ValoTheme.BUTTON_SMALL);

        projectCodeLabel.setCaption("Project code");
        registrationDateField.setCaption("Registration date");
        barcodeSendDateField.setCaption("Barcodes sent on");
    }

    @Override
    public Panel getProjectSheet() {
        return projectSheet;
    }

    @Override
    public void setDefaultContent() {
        Label defaultLabel = new Label("Click a project in the " +
                "table to get detailed content here!");
        projectSheet.setContent(defaultLabel);
    }

    @Override
    public void setProjectCode(String id) {
        this.projectCode = id;
    }

    @Override
    public void showProjectLayout() {
        final FormLayout projectLayout = new FormLayout();
        projectLayout.setSpacing(true);
        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        projectCodeLabel.setValue(this.projectCode);

        projectLayout.addComponent(projectCodeLabel);
        projectLayout.addComponent(registrationDateField);
        projectLayout.addComponent(barcodeSendDateField);
        buttonsLayout.addComponent(saveButton);
        buttonsLayout.addComponent(unfollowButton);
        projectLayout.addComponent(buttonsLayout);
        projectLayout.addStyleName("myformlayout-sheetcontent");

        this.projectSheet.setContent(projectLayout);
    }

    @Override
    public void setRegistrationDate(Date date) {
        this.registrationDate = date;
        this.registrationDateField.setValue(date);
    }

    @Override
    public void setBarcodeSentDate(Date date) {
        if (date != null) {
            this.barcodeSendDateField.setValue(date);
        }
    }

    @Override
    public Button getSaveButton() {
        return this.saveButton;
    }

    @Override
    public Button getUnfollowButton() {
        return this.unfollowButton;
    }

    @Override
    public DateField getRegistrationDateField() {
        return this.registrationDateField;
    }

    @Override
    public DateField getBarcodeSentDateField() {
        return this.barcodeSendDateField;
    }
}
