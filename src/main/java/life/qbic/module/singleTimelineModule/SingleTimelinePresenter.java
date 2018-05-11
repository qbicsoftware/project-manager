package life.qbic.module.singleTimelineModule;

import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.data.Item;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SingleTimelinePresenter {

  private SingleTimelineView view;
  private Item item;

  public SingleTimelinePresenter(Item item, SingleTimelineView view) {
    this.item = item;
    this.view = view;
  }

  private static long getDateDiff(Date date1, Date date2) {
    long diffInMillies = 0;
    if (date1.getTime() > date2.getTime()) {
      diffInMillies = date1.getTime() - date2.getTime();
    } else {
      diffInMillies = date2.getTime() - date1.getTime();
    }

    return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }

  public void update() {
    // Get project info
    Date currentDate = new Date();
    String projectID = item.getItemProperty("projectID").getValue().toString();
    Date projectRegisteredDate = (Date) item.getItemProperty("projectRegisteredDate").getValue();
    Date rawDataRegisteredDate = (Date) item.getItemProperty("rawDataRegistered").getValue();
    Date dataAnalyzedDate = (Date) item.getItemProperty("dataAnalyzedDate").getValue();

    // Create chart items
    createItem(projectID, rawDataRegisteredDate, dataAnalyzedDate, currentDate,
        projectRegisteredDate);

    view.drawChart();
  }

  public SingleTimelineView getChart() {
    return view;
  }

  private void createUnregisteredItem(String projectID, Date projectRegisteredDate,
      Date rawDataRegisteredDate, Date currentDate) {
    DataSeriesItem unregisteredItem = new DataSeriesItem();
    unregisteredItem.setName(projectID);
    unregisteredItem.setLow(projectRegisteredDate.getTime());
    if (rawDataRegisteredDate != null) {
      unregisteredItem.setHigh(rawDataRegisteredDate.getTime());
    } else {
      unregisteredItem.setHigh(currentDate.getTime());
    }
    view.getConf().getxAxis().addCategory(unregisteredItem.getName());
    view.getUnregisteredSeries().add(unregisteredItem);
  }

  private void createItem(String projectID, Date rawDataRegisteredDate, Date dataAnalyzedDate,
      Date currentDate, Date projectRegisteredDate) {
    createUnregisteredItem(projectID, projectRegisteredDate, rawDataRegisteredDate, currentDate);
    if (rawDataRegisteredDate != null) {
      DataSeriesItem intimeItem = new DataSeriesItem();
      intimeItem.setName(projectID);
      intimeItem.setLow(rawDataRegisteredDate.getTime());
      DataSeriesItem pottimeItem = new DataSeriesItem();
      Calendar c = Calendar.getInstance();
      c.setTime(rawDataRegisteredDate);
      c.add(Calendar.DATE, 42);
      Date overdueDate = c.getTime();
      pottimeItem.setName(projectID);
      pottimeItem.setLow(rawDataRegisteredDate.getTime());
      pottimeItem.setHigh(overdueDate.getTime());

      if (dataAnalyzedDate == null) {
        long diffRawToday = getDateDiff(rawDataRegisteredDate, currentDate);
        if (diffRawToday < 42) {
          intimeItem.setHigh(currentDate.getTime());
        } else {
          intimeItem.setHigh(overdueDate.getTime());
          DataSeriesItem overdueItem = new DataSeriesItem();
          overdueItem.setName(projectID);
          overdueItem.setLow(overdueDate.getTime());
          overdueItem.setHigh(currentDate.getTime());
          view.getOverdueSeries().add(overdueItem);
        }
      } else {
        long diffRawDataAnalyzed = getDateDiff(rawDataRegisteredDate, dataAnalyzedDate);
        if (diffRawDataAnalyzed < 42) {
          intimeItem.setHigh(dataAnalyzedDate.getTime());
          pottimeItem.setHigh(overdueDate.getTime());
        } else {
          intimeItem.setHigh(overdueDate.getTime());
          DataSeriesItem overdueItem = new DataSeriesItem();
          overdueItem.setName(projectID);
          overdueItem.setLow(overdueDate.getTime());
          overdueItem.setHigh(dataAnalyzedDate.getTime());
          view.getOverdueSeries().add(overdueItem);
        }
      }
      view.getPotentialtimeSeries().add(pottimeItem);
      view.getIntimeSeries().add(intimeItem);
    }
  }

}
