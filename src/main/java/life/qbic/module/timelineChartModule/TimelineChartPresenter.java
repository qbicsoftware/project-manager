package life.qbic.module.timelineChartModule;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import java.util.Collection;
import java.util.Date;
import life.qbic.ProjectContentModel;

public class TimelineChartPresenter {

  private TimelineChartView view;
  private ProjectContentModel model;

  public TimelineChartPresenter(ProjectContentModel model, TimelineChartView view) {
    this.model = model;
    this.view = view;
  }

  public void update() {
    SQLContainer tableContent = model.getTableContent();
    view.getUnregisteredSeries().clear();
    view.getIntimeSeries().clear();
    view.getOverdueSeries().clear();

    Collection<?> itemIds = tableContent.getItemIds();
    for (Object itemId : itemIds) {
      // Get project info
      String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue().toString();
      System.out.println(projectID);
      Date projectRegisteredDate = (Date) tableContent.getContainerProperty(itemId, "projectRegisteredDate").getValue();
      System.out.println(projectRegisteredDate);
      Date rawDataRegisteredDate = (Date) tableContent.getContainerProperty(itemId, "rawDataRegistered").getValue();
      System.out.println(rawDataRegisteredDate);

      // Create chart item
      DataSeriesItem item = new DataSeriesItem();
      item.setName(projectID);
      System.out.println(item.getName());
      item.setLow(projectRegisteredDate.getTime());
      if (rawDataRegisteredDate != null) {
        item.setHigh(rawDataRegisteredDate.getTime());
      } else {
        item.setHigh(new Date().getTime());
      }
      view.getUnregisteredSeries().add(item);
    }

    for (DataSeriesItem item : view.getUnregisteredSeries().getData()){
      System.out.println(item.getName());
    }
    view.drawChart();
  }

  public TimelineChartView getChart() {
    return view;
  }
}
