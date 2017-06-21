package life.qbic.projectOverviewModule;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import life.qbic.OpenBisConnection;
import life.qbic.database.ColumnTypes;
import life.qbic.database.ProjectDatabaseConnector;
import life.qbic.database.TableColumns;
import life.qbic.database.WrongArgumentSettingsException;
import org.apache.commons.logging.Log;
import org.vaadin.gridutil.GridUtil;
import org.vaadin.gridutil.cell.GridCellFilter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This presenter class will connect the UI with the underlying
 * logic. As this module will display the projectmanager database
 * content, the presenter will request data from the model, which handles
 * the SQL connection and contains the information to be shown.
 * Issues and Errors will be directed to the user via a notification message.
 */
public class ProjectOVPresenter{

    private final ProjectContentModel contentModel;

    private final Log log;

    private final ProjectOverviewModule overViewModule;

    private final String overviewTable = "projectsoverview";

    private final ObjectProperty<Boolean> overviewModuleChanged = new ObjectProperty<>(true);

    private final ObjectProperty<String> selectedProject = new ObjectProperty<>("");

    private final ProjectDatabaseConnector connection;

    private final OpenBisConnection openBisConnection;

    private final String portalURL = "https://portal.qbic.uni-tuebingen.de/portal/web/qbic/qnavigator#!project/";

    private Item selectedProjectItem = null;

    public ProjectOVPresenter(ProjectContentModel model,
                              ProjectOverviewModule overViewModule,
                              ProjectDatabaseConnector connection,
                              OpenBisConnection openBisConnection,
                              Log log){
        this.contentModel = model;
        this.log = log;
        this.overViewModule = overViewModule;
        this.connection = connection;
        this.openBisConnection = openBisConnection;
    }

    /**
     * Call and validate database connection of the business logic.
     */
    public void init() throws Exception{
        if (contentModel == null){
            log.error("The model was not instantiated yet!");
            return;
        }
        try{
            contentModel.init();
        } catch (SQLException exp) {
            log.error(exp);
            overViewModule.sendError("Database Error", "Could not connect to database :(");
            return;
        } catch (WrongArgumentSettingsException exp){
            log.error(exp);
            overViewModule.sendError("Database Error", "Could not connect to database");
            return;
        }

        log.info("Successfully connected to database");

        overViewModule.getOverviewGrid().setContainerDataSource(contentModel.getTableContent());

        overViewModule.getOverviewGrid().isChanged.addValueChangeListener(this::triggerViewPropertyChanged);

        overViewModule.getOverviewGrid().addItemClickListener(event -> {
            this.selectedProjectItem = event.getItem();
            this.selectedProject.setValue((String) event.getItem().getItemProperty(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID)).getValue());
            System.out.println("Selected project changed to: " + this.selectedProject.getValue());
        });

        renderTable();

    }

    /**
     * Beautify the grid
     */
    private void renderTable(){
        overViewModule.columnList = overViewModule.getOverviewGrid().getColumns();
        overViewModule.columnList.forEach((Grid.Column column) -> {
            String colName = column.getPropertyId().toString();
            if (colName.contains("Date") || overViewModule.columnHide.contains(colName)) {
                overViewModule.getOverviewGrid().removeColumn(colName);
            }
        });

        ColumnFieldTypes.clearFromParents();    // Clear from parent nodes (when reloading page)
        setFieldType("projectStatus", ColumnFieldTypes.PROJECTSTATUS);
        setFieldType("dataProcessed", ColumnFieldTypes.DATAPROCESSED);
        setFieldType("dataAnalyzed", ColumnFieldTypes.DATAANALYZED);
        setFieldType("reportSent", ColumnFieldTypes.REPORTSENT);
        setFieldType("rawDataRegistered", ColumnFieldTypes.RAWDATAREGISTERED);

        overViewModule.getOverviewGrid().setCellStyleGenerator(cellReference -> {
            if ("no".equals(cellReference.getValue())){
                return "v-grid-cell-no";
            }
            if ("in progress".equals(cellReference.getValue())){
                return "v-grid-cell-progress";
            }
            if ("done".equals(cellReference.getValue())){
                return "v-grid-cell-done";
            }
            if (cellReference.getPropertyId().equals("rawDataRegistered")){
                return GridUtil.ALIGN_CELL_CENTER;
            }
            return "v-grid-cell-normal";
        });

        final Grid.Column projectID = overViewModule.getOverviewGrid().
                getColumn(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID));

        projectID.setRenderer(new HtmlRenderer(), new Converter<String, String>() {

            @Override
            public String convertToModel(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
                return "not implemented";
            }

            @Override
            public String convertToPresentation(String project, Class<? extends String> aClass, Locale locale) throws ConversionException {
                String space = openBisConnection.getSpaceOfProject(project);
                return String.format("<a href='%s/%s/%s' target='_blank' style='color:black'>%s</a>", portalURL, space, project, project);
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });


        final GridCellFilter filter = new GridCellFilter(overViewModule.getOverviewGrid());
        configureFilter(filter);

        overViewModule.getOverviewGrid().getColumn("rawDataRegistered").
                setRenderer(new DateRenderer(new SimpleDateFormat("yyyy-MM-dd")));
        overViewModule.getOverviewGrid().getColumn("rawDataRegistered").setMaximumWidth(250d);
        overViewModule.getOverviewGrid().getColumn("projectID").setMaximumWidth(120d);
        overViewModule.getOverviewGrid().getColumn("offerID").setMaximumWidth(120d);
        //overViewModule.getOverviewGrid().getColumn("projectStatus").setMaximumWidth(140d);
        overViewModule.getOverviewGrid().getColumn("dataProcessed").setMaximumWidth(140d);
        overViewModule.getOverviewGrid().getColumn("dataAnalyzed").setMaximumWidth(140d);
        overViewModule.getOverviewGrid().getColumn("reportSent").setMaximumWidth(140d);
        overViewModule.getOverviewGrid().getColumn("invoice").setMaximumWidth(140d);

        // removes project status column #25
        overViewModule.getOverviewGrid().removeColumn("projectStatus");
    }

    /**
     * Configures the filter header in the grid
     * @param filter
     */
    private void configureFilter(GridCellFilter filter) {
        initExtraHeaderRow(overViewModule.getOverviewGrid(), filter);
        filter.setTextFilter("projectID", true, true);
        filter.setDateFilter("rawDataRegistered", new SimpleDateFormat("yyyy-MM-dd"), true);
        filter.setTextFilter("offerID", true, true);
        //final List<String> projectStatus = new ArrayList<>();
        //projectStatus.add("open");
        //projectStatus.add("in progress");
        //projectStatus.add("closed");
        //filter.setComboBoxFilter("projectStatus", projectStatus);
        final List<String> generalStatus = new ArrayList<>();
        generalStatus.add("no");
        generalStatus.add("in progress");
        generalStatus.add("closed");
        filter.setComboBoxFilter("dataProcessed", generalStatus);
        filter.setComboBoxFilter("dataAnalyzed", generalStatus);
        filter.setComboBoxFilter("reportSent", generalStatus);
        filter.setTextFilter("invoice", true, true);

    }

    /**
     * Implement the filter row in the header of the grid
     * @param grid The overview Grid reference
     * @param filter The GridCellFilter reference
     */
    private void initExtraHeaderRow(final Grid grid, final GridCellFilter filter){
        Grid.HeaderRow firstHeaderRow = grid.prependHeaderRow();
        // "projectStatus removed (#25)
        firstHeaderRow.join("projectID", "offerID", "rawDataRegistered",
                "dataProcessed", "dataAnalyzed", "reportSent", "invoice");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        firstHeaderRow.getCell("projectID").setComponent(buttonLayout);
        Button clearAllFilters = new Button("clearAllFilters", (Button.ClickListener) clickEvent ->
                filter.clearAllFilters());
        clearAllFilters.setIcon(FontAwesome.TIMES);
        clearAllFilters.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        buttonLayout.addComponent(clearAllFilters);
    }


    private void setFieldType(String columnID, Field fieldType){
        try{
            overViewModule.getOverviewGrid().getColumn(columnID).setEditorField(fieldType);
        } catch (Exception exp){
            log.error(String.format("Could not set editor field %s. Reason: %s", columnID, exp.getMessage()));
        }
    }

    /**
     * Query the status key figures from the model for the statistics overview
     * @return A map with the key figures
     */
    public Map<String, Double> getStatusKeyFigures(){
        return contentModel.getKeyFigures();
    }

    public void setFilter(String column, String filter){
        Container.Filter tmpFilter = new Like(column, filter);
        if(!contentModel.getTableContent().getContainerFilters().contains(tmpFilter)){
            //contentModel.getTableContent().removeContainerFilters("projectStatus");
            contentModel.getTableContent().addContainerFilter(new Like(column, filter));
        } else{
            contentModel.getTableContent().removeContainerFilter(tmpFilter);
        }

    }

    public void sendError(String caption, String message){
        overViewModule.sendError(caption, message);
    }

    public void sendInfo(String caption, String message){
        overViewModule.sendInfo(caption, message);
    }

    private void triggerViewPropertyChanged(Property.ValueChangeEvent event){
        this.contentModel.updateFigure();
        this.overviewModuleChanged.setValue(!overviewModuleChanged.getValue());

    }

    public ObjectProperty<Boolean> getIsChangedFlag(){
        return this.overviewModuleChanged;
    }

    public ObjectProperty<String> getSelectedProject() {return this.selectedProject;}

    public Item getSelectedProjectItem() {
        return this.selectedProjectItem;
    }

    /**
     * Refreshes the grid
     */
    public void refreshView() {
        try{
            // First, refresh the model (new SQL query!)
            this.contentModel.refresh();

            int timer = 0;

            /*
            If a content change happens, the editor is active.
            Since we are doing autocommit to the backend database,
            we have to wait until this is finished.
             */
            while(true){
                try{
                    this.overViewModule.getOverviewGrid().cancelEditor();
                } catch (Exception exp){

                }
                if (timer == 5){
                    break;
                } else if(!this.overViewModule.getOverviewGrid().isEditorActive()){
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(500);
                timer++;
            }

            // Second, update the grid
            /*
            The order of the next two lines is crucial!
            Do not change it, otherwise the grid will not
            be refreshed properly
             */
            this.overViewModule.init();
            this.overViewModule.getOverviewGrid().setContainerDataSource(this.contentModel.getTableContent());
        } catch (Exception exc){
            log.error("Could not refresh the project overview model.", exc);
        }
    }

    public boolean isProjectInFollowingTable(String projectCode) {

        String query = String.format("SELECT * FROM %s WHERE projectID=\'%s\'", overviewTable, projectCode);

        JDBCConnectionPool pool = connection.getConnectionPool();
        Connection conn = null;
        try{
            conn = pool.reserveConnection();
        } catch (SQLException exc){
            log.error("Could not reserve a SQL connection.", exc);
        }

        int size = 0;

        try {
            if (conn != null) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                try {
                    resultSet.last();
                    size = resultSet.getRow();
                    System.out.println(size);
                    resultSet.beforeFirst();
                }
                catch(Exception ex) {
                    size = 0;
                }
                statement.close();
                conn.commit();
            }
        } catch (SQLException exc){
            log.error("Exception during statement creation!", exc);
        } finally {
            pool.releaseConnection(conn);
        }
        return size > 0;

    }

    public void createNewProjectEntry(String selectedProject) {

        String query = String.format("INSERT INTO %s (projectID) VALUES (\'%s\')", overviewTable, selectedProject);

        JDBCConnectionPool pool = connection.getConnectionPool();
        Connection conn = null;
        try{
            conn = pool.reserveConnection();
        } catch (SQLException exc){
            log.error("Could not reserve a SQL connection.", exc);
        }

        try {
            if (conn != null) {
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                statement.close();
                conn.commit();
            }
        } catch (SQLException exc){
            log.error("Exception during statement creation!", exc);
        } finally {
            pool.releaseConnection(conn);
        }

    }

    public Map<String, Integer> getTimeLineStats(){
        return this.contentModel.getProjectesTimeLineStats();
    }
}