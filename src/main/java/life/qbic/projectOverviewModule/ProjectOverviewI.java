package life.qbic.projectOverviewModule;

import com.vaadin.ui.Grid;
import java.util.List;
import life.qbic.MyGrid;

/**
 * Created by sven1103 on 8/12/16.
 */
public interface ProjectOverviewI {

  MyGrid getOverviewGrid();

  List<Grid.Column> getColumnList();

}
