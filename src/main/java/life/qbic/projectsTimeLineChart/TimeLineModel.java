package life.qbic.projectsTimeLineChart;

import com.vaadin.addon.charts.model.ListSeries;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sven1103 on 23/01/17.
 */
public final class TimeLineModel implements TimeLineStats {

    private Map<String, Integer> categories;

    public TimeLineModel(){}


    @Override
    public void setCategories(String... categories) {
        this.categories = new HashMap<>();
        for (String category : categories){
            this.categories.put(category, 0);
        }

    }

    @Override
    public void setNumberForCategory(String category, int number) {
        this.categories.put(category, number);
    }

    @Override
    public String[] getCategories() {
        if (categories != null){
            return this.categories.keySet().toArray(new String[this.categories.size()]);
        }
        return new String[0];
    }

    @Override
    public ListSeries getValues() {
        return new ListSeries("Following projects", this.categories.values().toArray(new Integer[this.categories.size()]));
    }

    @Override
    public void setCategories(Map<String, Integer> statistics) {
        this.categories = statistics;
    }
}
