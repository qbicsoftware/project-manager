package life.qbic.projectsTimeLineChart;

import java.util.Map;

/**
 * Created by sven1103 on 24/01/17.
 */
public class TimeLineChartPresenter {


  private final TimeLineStats timeLineModel;
  private final TimeLineChart chart;

  public TimeLineChartPresenter(TimeLineStats timeLineModel, TimeLineChart chart) {
    this.timeLineModel = timeLineModel;
    this.chart = chart;
  }

  public void setCategories(Map<String, Integer> stats) {
    this.timeLineModel.setCategories(stats);

    this.chart.setCategories("Project age", timeLineModel.getCategories());
    this.chart.setYaxis("Number of projects");
    this.chart.addSeries(timeLineModel.getValues());
    this.chart.createChart();
  }

  public void updateData(Map<String, Integer> stats) {
    this.timeLineModel.setCategories(stats);
    this.chart.updateChart(timeLineModel.getValues());
  }


}
