package life.qbic.projectsTimeLineChart;

import com.vaadin.addon.charts.model.ListSeries;

import java.util.Map;

/**
 * Created by sven1103 on 23/01/17.
 */
public interface TimeLineStats {

    void setCategories(String ... categories);

    void setNumberForCategory(String category, int number);

    String[] getCategories();

    ListSeries getValues();

    void setCategories(Map<String, Integer> statistics);

}
