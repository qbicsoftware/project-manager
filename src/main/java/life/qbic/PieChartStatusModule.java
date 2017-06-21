package life.qbic;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sven1103 on 29/11/16.
 */
public class PieChartStatusModule extends Chart {

    private final Configuration conf;

    private final PlotOptionsPie plotOptions = new PlotOptionsPie();

    private final DataSeries dataSeries = new DataSeries();

    private Map<String, DataSeriesItem> seriesMap = new HashMap<>();

    public PieChartStatusModule() {
        super(ChartType.PIE);
        conf = this.getConfiguration();
        init();
    }

    private void init() {
        dataSeries.clear();

        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setAnimation(true);

        conf.setPlotOptions(plotOptions);

        seriesMap.forEach((key, value) -> dataSeries.add(value));

        conf.addSeries(dataSeries);
        conf.setTitle("Projects status");
        conf.setSubTitle("Overview");

        drawChart(conf);
    }

    public void update(String itemName, Double itemValue) {
        seriesMap.put(itemName, new DataSeriesItem(itemName, itemValue));
        refresh();
    }

    private void refresh() {
        if (this.plotOptions != null && plotOptions.getAnimation()) {
            plotOptions.setAnimation(false);
        }
        dataSeries.clear();

        Iterator<Color> dataSeriesColors = DataSeriesColors.SHADELIST.iterator();

        seriesMap.forEach((String key, DataSeriesItem item) -> {
            item.setColor(dataSeriesColors.next());
            dataSeries.add(item, true, false);
        });

        drawChart(conf);
    }

    public String getDataSeriesObject(PointClickEvent event) {
        return this.dataSeries.get(event.getPointIndex()).getName();
    }

    public DataSeriesItem getDataSeriesItem(PointClickEvent event) {
        return this.dataSeries.get(event.getPointIndex());
    }


}
