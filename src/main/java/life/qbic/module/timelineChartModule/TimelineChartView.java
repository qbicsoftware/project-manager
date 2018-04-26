package life.qbic.module.timelineChartModule;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabelsRange;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsColumnrange;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import java.util.Collection;
import java.util.Date;

@SuppressWarnings("serial")
public class TimelineChartView extends Chart {

  private DataSeries unregisteredSeries, intimeSeries, overdueSeries;

  private Configuration conf;

  private Date currentDate = new Date();

  @Override
  public String getDescription() {
    return "Shows the progress of each project";
  }

  public TimelineChartView () {
    super(ChartType.COLUMNRANGE);
    conf = this.getConfiguration();
    conf.setTitle("Project Timeline");
    conf.getChart().setInverted(true);

    XAxis xAxis = new XAxis();
    xAxis.setCategories("Project 1", "Project 2");
    conf.addxAxis(xAxis);

    YAxis yAxis = new YAxis();
    yAxis.setTitle("Time");
    yAxis.setType(AxisType.DATETIME);
    conf.addyAxis(yAxis);

    Tooltip tooltip = new Tooltip();
    tooltip.setFormatter("this.dataseries.name +': '+ Highcharts.dateFormat('YYYY/mm/dd', this.point.low) + ' - ' + Highcharts.dateFormat('YYYY/mm/dd', this.point.high)");
    conf.setTooltip(tooltip);

    PlotOptionsColumnrange columnRange = new PlotOptionsColumnrange();
    columnRange.setGrouping(false);
    DataLabelsRange dataLabels = new DataLabelsRange(true);
    dataLabels
        .setFormatter("this.y == this.point.low ? '' : this.series.name");
    dataLabels.setInside(true);
    dataLabels.setColor(new SolidColor("white"));
    columnRange.setDataLabels(dataLabels);

    conf.setPlotOptions(columnRange);

    unregisteredSeries = new DataSeries();
    unregisteredSeries.setName("Unregistered");
    PlotOptionsColumnrange o = new PlotOptionsColumnrange();
    o.setColor(new SolidColor("#ff9a00"));
    unregisteredSeries.setPlotOptions(o);

    overdueSeries = new DataSeries();
    o = new PlotOptionsColumnrange();
    o.setColor(new SolidColor("#c20047"));
    overdueSeries.setPlotOptions(o);
    overdueSeries.setName("Overdue");

    intimeSeries = new DataSeries();
    o = new PlotOptionsColumnrange();
    o.setColor(new SolidColor("#26A65B"));
    intimeSeries.setPlotOptions(o);
    intimeSeries.setName("In time");

    conf.setSeries(unregisteredSeries, intimeSeries, overdueSeries);

    this.drawChart();
  }

  public DataSeries getUnregisteredSeries() {
    return unregisteredSeries;
  }

  public DataSeries getIntimeSeries() {
    return intimeSeries;
  }

  public DataSeries getOverdueSeries() {
    return overdueSeries;
  }
}