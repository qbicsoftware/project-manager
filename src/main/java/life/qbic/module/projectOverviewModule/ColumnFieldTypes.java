package life.qbic.module.projectOverviewModule;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import java.util.ArrayList;

/**
 * Created by sven1103 on 28/11/16.
 */
public class ColumnFieldTypes {

  private ComboBox PROJECTSTATUS = new ComboBox();
  private ComboBox PROJECTREGISTERED = new ComboBox();
  private ComboBox BARCODESENT = new ComboBox();
  private ComboBox DATAPROCESSED = new ComboBox();
  private ComboBox DATAANALYZED = new ComboBox();
  private ComboBox REPORTSENT = new ComboBox();
  private DateField RAWDATAREGISTERED = new DateField();
  private ArrayList<Field> fields = new ArrayList<>();

  public ColumnFieldTypes() {
    initProjectStatus();
    initProjectRegistered();
    initBarcodeSent();
    initDataProcessed();
    initDataAnalyzed();
    initReportSent();
    initRawDataRegistered();
    initFields();
  }

  private void initProjectStatus() {
    PROJECTSTATUS.addItem("open");
    PROJECTSTATUS.addItem("completed");
  }

  private void initProjectRegistered() {
    PROJECTREGISTERED.addItem("no");
    PROJECTREGISTERED.addItem("in progress");
    PROJECTREGISTERED.addItem("done");
  }

  private void initBarcodeSent() {
    BARCODESENT.addItem("no");
    BARCODESENT.addItem("in progress");
    BARCODESENT.addItem("done");
  }

  private void initDataProcessed() {
    DATAPROCESSED.addItem("no");
    DATAPROCESSED.addItem("in progress");
    DATAPROCESSED.addItem("done");
  }

  private void initDataAnalyzed() {
    DATAANALYZED.addItem("no");
    DATAANALYZED.addItem("in progress");
    DATAANALYZED.addItem("done");
  }

  private void initReportSent() {
    REPORTSENT.addItem("no");
    REPORTSENT.addItem("in progress");
    REPORTSENT.addItem("done");
  }

  private void initRawDataRegistered() {
    RAWDATAREGISTERED.setDateFormat("yyyy-MM-dd");
  }

  private void initFields() {
    fields.add(PROJECTSTATUS);
    fields.add(PROJECTREGISTERED);
    fields.add(BARCODESENT);
    fields.add(DATAPROCESSED);
    fields.add(DATAANALYZED);
    fields.add(REPORTSENT);
  }

  public void clearFromParents() {
    fields.forEach((Field field) ->
        field.setParent(null));
  }

  public ComboBox getPROJECTSTATUS() {
    return PROJECTSTATUS;
  }

  public ComboBox getPROJECTREGISTERED() {
    return PROJECTREGISTERED;
  }

  public ComboBox getBARCODESENT() {
    return BARCODESENT;
  }

  public ComboBox getDATAPROCESSED() {
    return DATAPROCESSED;
  }

  public ComboBox getDATAANALYZED() {
    return DATAANALYZED;
  }

  public ComboBox getREPORTSENT() {
    return REPORTSENT;
  }

  public DateField getRAWDATAREGISTERED() {
    return RAWDATAREGISTERED;
  }

  public ArrayList<Field> getFields() {
    return fields;
  }

}
