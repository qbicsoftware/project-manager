package life.qbic.module.overviewChartModule;

import com.vaadin.addon.charts.model.DataSeriesItem;
import java.util.Map;
import life.qbic.ProjectContentModel;

public class OverviewChartPresenter {

  private OverviewChartView view;
  private ProjectContentModel model;

  public OverviewChartPresenter(ProjectContentModel model, OverviewChartView view) {
    this.model = model;
    this.view = view;
    update();
  }

  public void update() {
    Map<String, Integer> status = model.getProjectsTimeLineStats();
    view.getSeries().clear();
    for (String key : status.keySet()) {
      view.getSeries().add(new DataSeriesItem(key, status.get(key)));
    }
    view.drawChart();
  }
}
