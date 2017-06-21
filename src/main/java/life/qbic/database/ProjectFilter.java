package life.qbic.database;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sven1103 on 16/01/17.
 */
public class ProjectFilter {

    private List<Container.Filter> filterList;

    public void createFilter(String id, List<String> projectList) {

        filterList = new ArrayList<>();

        projectList.forEach(project -> {
            filterList.add(new Compare.Equal(id, project));
        });

    }


    public Container.Filter[] getFilterList() {
        return this.filterList.toArray(new Container.Filter[filterList.size()]);
    }
}
