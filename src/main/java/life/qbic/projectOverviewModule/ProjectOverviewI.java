package life.qbic.projectOverviewModule;

import com.vaadin.ui.Grid;
import life.qbic.MyGrid;

import java.util.List;

/**
 * Created by sven1103 on 8/12/16.
 */
public interface ProjectOverviewI {

    MyGrid getOverviewGrid();

    List<Grid.Column> getColumnList();

}
