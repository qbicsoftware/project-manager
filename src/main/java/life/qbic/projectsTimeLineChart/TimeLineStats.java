package life.qbic.projectsTimeLineChart;

import com.vaadin.addon.charts.model.ListSeries;
import java.util.Map;

/**
 * Created by sven1103 on 23/01/17.
 */
public interface TimeLineStats {

  void setCategories(String... categories);


  String[] getCategories();

  void setCategories(Map<String, Integer> statistics);

  ListSeries getValues();

}
