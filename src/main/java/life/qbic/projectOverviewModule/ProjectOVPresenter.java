package life.qbic.projectOverviewModule;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import life.qbic.ProjectContentModel;
import life.qbic.database.projectInvestigatorDB.ColumnTypes;
import life.qbic.database.projectInvestigatorDB.ProjectDatabaseConnector;
import life.qbic.database.projectInvestigatorDB.TableColumns;
import life.qbic.database.projectInvestigatorDB.WrongArgumentSettingsException;
import life.qbic.openbis.OpenBisConnection;
import org.apache.commons.logging.Log;
import org.vaadin.gridutil.cell.GridCellFilter;

/**
 * This presenter class will connect the UI with the underlying logic. As this module will display
 * the projectmanager database content, the presenter will request data from the model, which
 * handles the SQL connection and contains the information to be shown. Issues and Errors will be
 * directed to the user via a notification message.
 */
public class ProjectOVPresenter {

  private final ProjectContentModel contentModel;

  private final Log log;

  private final ProjectOverviewModule overViewModule;

  private final String overviewTable = "projectsoverview";

  private final ObjectProperty<Boolean> overviewModuleChanged = new ObjectProperty<>(true);

  private final ObjectProperty<String> selectedProject = new ObjectProperty<>("");

  private final ProjectDatabaseConnector connection;

  private final OpenBisConnection openBisConnection;

  private final Button unfollowButton = new Button("Unfollow");

  private final String portalURL = "https://portal.qbic.uni-tuebingen.de/portal/web/qbic/qnavigator#!project/";
  private final ColumnFieldTypes columnFieldTypes;
  private Item selectedProjectItem = null;

  public ProjectOVPresenter(ProjectContentModel model,
      ProjectOverviewModule overViewModule,
      ProjectDatabaseConnector connection,
      OpenBisConnection openBisConnection,
      Log log) {
    this.contentModel = model;
    this.log = log;
    this.overViewModule = overViewModule;
    this.connection = connection;
    this.openBisConnection = openBisConnection;
    columnFieldTypes = new ColumnFieldTypes();

    unfollowButton.setIcon(FontAwesome.MINUS_CIRCLE);
    unfollowButton.setStyleName(ValoTheme.BUTTON_DANGER);
    unfollowButton.setEnabled(false);
  }

  /**
   * Call and validate database connection of the business logic.
   */
  public void init() throws Exception {
    if (contentModel == null) {
      log.error("The model was not instantiated yet!");
      return;
    }
    try {
      contentModel.init();
    } catch (SQLException exp) {
      log.error(exp);
      overViewModule.sendError("Database Error", "Could not connect to database :(");
      return;
    } catch (WrongArgumentSettingsException exp) {
      log.error(exp);
      overViewModule.sendError("Database Error", "Could not connect to database");
      return;
    }

    log.info("Successfully connected to database");
    if (contentModel.getFollowingProjects().size() == 0) {
      this.overViewModule.noProjectMessage();
    } else {
      this.overViewModule.getOverviewGrid()
          .setContainerDataSource(this.contentModel.getTableContent());
      this.overViewModule.showGrid();
      overViewModule.getOverviewGrid().isChanged
          .addValueChangeListener(this::triggerViewPropertyChanged);

      overViewModule.getOverviewGrid().addItemClickListener(event -> {
        this.selectedProjectItem = event.getItem();
        this.selectedProject.setValue((String) event.getItem()
            .getItemProperty(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID))
            .getValue());
        log.info("Selected project changed to: " + this.selectedProject.getValue());
      });
    }

    selectedProject.addValueChangeListener((ValueChangeListener) event -> {
      if (selectedProject.getValue() != null) {
        unfollowButton.setEnabled(true);
      } else {
        unfollowButton.setEnabled(false);
      }
    });

    renderTable();

  }

  /**
   * Beautify the grid
   */
  private void renderTable() {
    overViewModule.getOverviewGrid().setSizeFull();
    overViewModule.columnList = overViewModule.getOverviewGrid().getColumns();
    overViewModule.getOverviewGrid().setResponsive(true);
    overViewModule.getOverviewGrid().removeAllColumns();
    overViewModule.getOverviewGrid().addColumn("projectID").setHeaderCaption("Project");
    overViewModule.getOverviewGrid().addColumn("investigatorName")
        .setHeaderCaption("Principal Investigator");
    overViewModule.getOverviewGrid().addColumn("projectRegisteredDate")
        .setHeaderCaption("Project Registered");
    overViewModule.getOverviewGrid().addColumn("rawDataRegistered")
        .setHeaderCaption("Raw Data Registered");
    overViewModule.getOverviewGrid().addColumn("dataAnalyzedDate")
        .setHeaderCaption("Data Analyzed");
    overViewModule.getOverviewGrid().getColumn("projectID").setEditable(false);
    overViewModule.getOverviewGrid().getColumn("investigatorName").setEditable(false);
    overViewModule.getOverviewGrid().getColumn("projectRegisteredDate").setEditable(false);
    overViewModule.getOverviewGrid().getColumn("rawDataRegistered").setEditable(false);
    overViewModule.getOverviewGrid().getColumn("dataAnalyzedDate").setEditable(false);
    overViewModule.getOverviewGrid().addColumn("offerID").setHeaderCaption("Offer");
    overViewModule.getOverviewGrid().addColumn("invoice").setHeaderCaption("Invoice");
    overViewModule.getOverviewGrid().getColumn("offerID").setEditable(true);
    overViewModule.getOverviewGrid().getColumn("invoice").setEditable(true);
    columnFieldTypes.clearFromParents();    // Clear from parent nodes (when reloading page)

    final Grid.Column projectID = overViewModule.getOverviewGrid().
        getColumn(TableColumns.PROJECTOVERVIEWTABLE.get(ColumnTypes.PROJECTID));

    projectID.setRenderer(new HtmlRenderer(), new Converter<String, String>() {

      @Override
      public String convertToModel(String s, Class<? extends String> aClass, Locale locale)
          throws ConversionException {
        return "not implemented";
      }

      @Override
      public String convertToPresentation(String project, Class<? extends String> aClass,
          Locale locale) throws ConversionException {
        String space = openBisConnection.getSpaceOfProject(project);
        return String
            .format("<a href='%s/%s/%s' target='_blank' style='color:black'>%s</a>", portalURL,
                space, project, project);
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
    overViewModule.getOverviewGrid().getColumn("projectRegisteredDate").
        setRenderer(new DateRenderer(new SimpleDateFormat("yyyy-MM-dd")));
    overViewModule.getOverviewGrid().getColumn("dataAnalyzedDate").
        setRenderer(new DateRenderer(new SimpleDateFormat("yyyy-MM-dd")));

    for (Column column : overViewModule.getOverviewGrid().getColumns()) {
      if (column.getHeaderCaption().equals("Principal Investigator") ||
          column.getHeaderCaption().equals("Offer") ||
          column.getHeaderCaption().equals("Invoice")) {
        column.setWidth(230);
      } else if (column.getHeaderCaption().equals("Project")) {
        column.setWidth(110);
      }
      else {
        column.setWidth(180);
      }
    }
    overViewModule.getOverviewGrid().setFrozenColumnCount(1);
  }

  /**
   * Configures the filter header in the grid
   */
  private void configureFilter(GridCellFilter filter) {
    initExtraHeaderRow(overViewModule.getOverviewGrid(), filter);
    filter.setTextFilter("projectID", true, false);
    filter.setDateFilter("rawDataRegistered", new SimpleDateFormat("yyyy-MM-dd"), true);
    filter.setDateFilter("projectRegisteredDate", new SimpleDateFormat("yyyy-MM-dd"), true);
    filter.setDateFilter("dataAnalyzedDate", new SimpleDateFormat("yyyy-MM-dd"), true);
    filter.setTextFilter("offerID", true, false);
    filter.setTextFilter("investigatorName", true, false);
    filter.setTextFilter("invoice", true, false);

  }

  /**
   * Implement the filter row in the header of the grid
   *
   * @param grid The overview Grid reference
   * @param filter The GridCellFilter reference
   */
  private void initExtraHeaderRow(final Grid grid, final GridCellFilter filter) {
    Grid.HeaderRow firstHeaderRow = grid.prependHeaderRow();
    // "projectStatus removed (#25)
    firstHeaderRow.join("projectID", "investigatorName", "projectRegisteredDate",
        "rawDataRegistered", "dataAnalyzedDate", "offerID", "invoice");
    HorizontalLayout buttonLayout = new HorizontalLayout();
    buttonLayout.setSpacing(true);
    firstHeaderRow.getCell("projectID").setComponent(buttonLayout);
    Button clearAllFilters = new Button("clear All Filters", (Button.ClickListener) clickEvent ->
        filter.clearAllFilters());
    clearAllFilters.setIcon(FontAwesome.TIMES);
    clearAllFilters.addStyleName(ValoTheme.BUTTON_PRIMARY);
    buttonLayout.addComponents(clearAllFilters, unfollowButton);
    buttonLayout.setComponentAlignment(unfollowButton, Alignment.MIDDLE_LEFT);
  }


  private void setFieldType(String columnID, Field fieldType) {
    try {
      overViewModule.getOverviewGrid().getColumn(columnID).setEditorField(fieldType);
    } catch (Exception exp) {
      log.error(
          String.format("Could not set editor field %s. Reason: %s", columnID, exp.getMessage()));
    }
  }

  public void setFilter(String column, String filter) {
    Container.Filter tmpFilter = new Like(column, filter);
    if (!contentModel.getTableContent().getContainerFilters().contains(tmpFilter)) {
      //contentModel.getTableContent().removeContainerFilters("projectStatus");
      contentModel.getTableContent().addContainerFilter(new Like(column, filter));
    } else {
      contentModel.getTableContent().removeContainerFilter(tmpFilter);
    }

  }

  public void sendError(String caption, String message) {
    overViewModule.sendError(caption, message);
  }

  public void sendInfo(String caption, String message) {
    overViewModule.sendInfo(caption, message);
  }

  private void triggerViewPropertyChanged(Property.ValueChangeEvent event) {
    this.contentModel.update();
    this.overviewModuleChanged.setValue(!overviewModuleChanged.getValue());

  }

  public ObjectProperty<Boolean> getIsChangedFlag() {
    return this.overviewModuleChanged;
  }

  public ObjectProperty<String> getSelectedProject() {
    return this.selectedProject;
  }

  public Item getSelectedProjectItem() {
    return this.selectedProjectItem;
  }

  /**
   * Refreshes the grid
   */
  public void refreshView() {
    try {
      // First, refresh the model (new SQL query!)
      this.contentModel.refresh();

      int timer = 0;

            /*
            If a content change happens, the editor is active.
            Since we are doing autocommit to the backend database,
            we have to wait until this is finished.
             */
      while (true) {
        try {
          this.overViewModule.getOverviewGrid().cancelEditor();
        } catch (Exception exp) {

        }
        if (timer == 5) {
          break;
        } else if (!this.overViewModule.getOverviewGrid().isEditorActive()) {
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
      log.info(contentModel.getFollowingProjects().size() + " Projects are followed currently");
      if (contentModel.getFollowingProjects().size() == 0) {
        this.overViewModule.noProjectMessage();
      } else {
        this.overViewModule.getOverviewGrid()
            .setContainerDataSource(this.contentModel.getTableContent());
        this.overViewModule.showGrid();
      }

    } catch (Exception exc) {
      log.error("Could not refresh the project overview model.", exc);
    }
  }

  public boolean isProjectInFollowingTable(String projectCode) {

    String query = String
        .format("SELECT * FROM %s WHERE projectID=\'%s\'", overviewTable, projectCode);

    JDBCConnectionPool pool = connection.getConnectionPool();
    Connection conn = null;
    try {
      conn = pool.reserveConnection();
    } catch (SQLException exc) {
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
          resultSet.beforeFirst();
        } catch (Exception ex) {
          size = 0;
        }
        statement.close();
        conn.commit();
      }
    } catch (SQLException exc) {
      log.error("Exception during statement creation!", exc);
    } finally {
      pool.releaseConnection(conn);
    }
    return size > 0;

  }

  public void createNewProjectEntry(String selectedProject) {

    String query = String
        .format("INSERT INTO %s (projectID) VALUES (\'%s\')", overviewTable, selectedProject);

    JDBCConnectionPool pool = connection.getConnectionPool();
    Connection conn = null;
    try {
      conn = pool.reserveConnection();
    } catch (SQLException exc) {
      log.error("Could not reserve a SQL connection.", exc);
    }

    try {
      if (conn != null) {
        Statement statement = conn.createStatement();
        statement.executeUpdate(query);
        statement.close();
        conn.commit();
      }
    } catch (SQLException exc) {
      log.error("Exception during statement creation!", exc);
    } finally {
      pool.releaseConnection(conn);
    }

  }

  public void clearSelection() {
    overViewModule.getOverviewGrid().getSelectionModel().reset();
    unfollowButton.setEnabled(false);
  }

  public Map<String, Integer> getTimeLineStats() {
    return this.contentModel.getProjectsTimeLineStats();
  }

  public Button getUnfollowButton() {
    return unfollowButton;
  }
}