package life.qbic.overviewChart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.SolidColor;

public class OverviewChartView extends Chart {

    private DataSeries series;

    private Configuration conf;

    private PlotOptionsPie plotOptions;


    public OverviewChartView() {
        super(ChartType.PIE);
        conf = this.getConfiguration();
        series = new DataSeries();

        plotOptions = new PlotOptionsPie();
        plotOptions.setShowInLegend(true);
        // unregistered - in time - overdue
        plotOptions.setColors(new SolidColor("#ff9a00"), new SolidColor("#26A65B"), new SolidColor("#c20047"));
        plotOptions.setSize("100px");
        this.setHeight("300px");
        this.setWidth("400px");
        conf.setPlotOptions(plotOptions);
        conf.setTitle("Project Manager");
        conf.setSubTitle("Project Status");
        conf.getChart().setBackgroundColor(new SolidColor("#fafafa"));

        series.setName("projects");
        conf.setSeries(series);
        this.drawChart(conf);

    }

    public DataSeries getSeries() {
        return series;
    }
}
