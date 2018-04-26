package life.qbic.module.timelineChartModule;

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
      DataSeriesItem item;
      String projectID = tableContent.getContainerProperty(itemId, "projectID").getValue().toString();
      Date projectRegisteredDate = (Date) tableContent.getContainerProperty(itemId, "projectRegisteredDate").getValue();
      Date rawDataRegisteredDate = (Date) tableContent.getContainerProperty(itemId, "rawDataRegistered").getValue();
      item = new DataSeriesItem();
      item.setName(projectID);
      item.setLow(projectRegisteredDate.getTime());
      if (rawDataRegisteredDate != null) {
        item.setHigh(rawDataRegisteredDate.getTime());
      } else {
        item.setHigh(new Date().getTime());
      }

      view.getUnregisteredSeries().add(item);

//      long dataAnalyzedDate = rawDataRegisteredDate + 100000000 * 10;
//      item = new DataSeriesItem();
//      item.setName("Project 1");
//      item.setLow(rawDataRegisteredDate);
//      item.setHigh(dataAnalyzedDate);
//      intimeSeries.add(item);
//
//      item = new DataSeriesItem();
//      item.setName("Project 1");
//      item.setLow(dataAnalyzedDate);
//      item.setHigh(dataAnalyzedDate + 100000000 * 10);
//      overdueSeries.add(item);

      view.drawChart();
    }
  }

  public TimelineChartView getChart() { return view;
  }
}
