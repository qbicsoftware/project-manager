package life.qbic.portal.projectsTimeLineChart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;

import java.util.List;


/**
 * Created by sven1103 on 23/01/17.
 */
public class TimeLineChart extends Chart {

    private final Configuration configuration;

    private final XAxis xAxis;

    private final YAxis yAxis;

    private List<Series> statsSeries;

    public TimeLineChart(){
        super(ChartType.COLUMN);
        configuration = this.getConfiguration();
        xAxis = new XAxis();
        yAxis = new YAxis();
    }

    public TimeLineChart setTitle(String title){
        configuration.setTitle(title);
        return this;
    }

    public TimeLineChart setCategories(String title, String... categories ){
        xAxis.setCategories(categories);
        xAxis.setTitle(title);
        configuration.addxAxis(xAxis);
        return this;
    }

    public TimeLineChart addSeries(ListSeries ... listSeries){
        for (ListSeries series : listSeries){
            configuration.addSeries(series);
        }
        return this;
    }

    public TimeLineChart setYaxis(String title){
        yAxis.setTitle(title);
        configuration.addyAxis(yAxis);
        return this;
    }

    public TimeLineChart createChart(){
        this.drawChart(configuration);
        return this;
    }

    public void updateChart(ListSeries listSeries){
        configuration.setSeries(listSeries);
        createChart();
    }


}
