package life.qbic.module.timelineChartModule;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumnrange;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import java.util.Calendar;
import java.util.Date;

public class TimelineChartView extends Chart {

  private DataSeries unregisteredSeries, intimeSeries, overdueSeries, potentialtimeSeries;

  private Configuration conf;

  public TimelineChartView() {
    super(ChartType.COLUMNRANGE);
    conf = this.getConfiguration();
    conf.setTitle("Timeline");
    conf.getChart().setInverted(true);

    XAxis xAxis = new XAxis();
    conf.addxAxis(xAxis);

    YAxis yAxis = new YAxis();
    yAxis.setType(AxisType.DATETIME);
    yAxis.setTitle("Time");
    Calendar cal = Calendar.getInstance();
    yAxis.setMax(new Date().getTime());
    cal.add(Calendar.YEAR, -3);
    yAxis.setMin(cal.getTime());
    conf.addyAxis(yAxis);
    this.setSizeFull();

    Tooltip tooltip = new Tooltip();
    tooltip.setFormatter(
        "this.dataseries.name +': '+ Highcharts.dateFormat('YYYY/mm/dd', this.point.low) + ' - ' + Highcharts.dateFormat('YYYY/mm/dd', this.point.high)");
    conf.setTooltip(tooltip);

    PlotOptionsColumnrange columnRange = new PlotOptionsColumnrange();
    columnRange.setCursor(Cursor.POINTER);
    //columnRange.setAllowPointSelect(true);
    columnRange.setGrouping(false);

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

    potentialtimeSeries = new DataSeries();
    o = new PlotOptionsColumnrange();
    o.setColor(new SolidColor("#79A65B"));
    potentialtimeSeries.setPlotOptions(o);
    potentialtimeSeries.setName("pot. time");

    conf.getChart().setBackgroundColor(new SolidColor("#fafafa"));
    conf.addSeries(potentialtimeSeries);
    conf.addSeries(unregisteredSeries);
    conf.addSeries(overdueSeries);
    conf.addSeries(intimeSeries);

    this.drawChart();
  }

  @Override
  public String getDescription() {
    return "Shows the progress of each project";
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

  public Configuration getConf() {
    return conf;
  }

  public DataSeries getPotentialtimeSeries() {
    return potentialtimeSeries;
  }
}