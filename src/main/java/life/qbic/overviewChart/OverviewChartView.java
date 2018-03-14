package life.qbic.overviewChart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OverviewChartView extends Chart{

        private DataSeries series;

        private Configuration conf;

        private PlotOptionsPie plotOptions;


        public OverviewChartView() {
            super(ChartType.PIE);
            conf = this.getConfiguration();
            series = new DataSeries();

            plotOptions = new PlotOptionsPie();
            plotOptions.setShowInLegend(false);
            plotOptions.setColors(new SolidColor("#e67e22"), new SolidColor("#2c9720"), new SolidColor("#ed473b"));

            conf.setPlotOptions(plotOptions);

            series.setName("projects");
            conf.setSeries(series);
            this.drawChart(conf);

        }

        public DataSeries getSeries() {
            return series;
        }
}
