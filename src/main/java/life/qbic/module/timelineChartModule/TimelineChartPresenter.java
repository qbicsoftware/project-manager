package life.qbic.module.timelineChartModule;

import com.vaadin.addon.charts.model.DataSeriesItem;
import java.util.Map;
import life.qbic.ProjectContentModel;

public class TimelineChartPresenter {

  private TimelineChartView view;
  private ProjectContentModel model;

  public TimelineChartPresenter(ProjectContentModel model, TimelineChartView view) {
    this.model = model;
    this.view = view;
    update();
  }

  public void update() {
//    Map<String, Integer> status = model.getProjectsTimeLineStats();
//    view.getSeries().clear();
//    for (String key : status.keySet()) {
//      view.getSeries().add(new DataSeriesItem(key, status.get(key)));
//    }
//    view.drawChart();
  }

  public TimelineChartView getChart() {
    return view;
  }
}
