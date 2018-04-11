package life.qbic.module.projectOverviewModule;

import com.vaadin.ui.Grid;
import java.util.List;

/**
 * Created by sven1103 on 8/12/16.
 */
public interface ProjectOverviewI {

  ProjectOverviewGrid getOverviewGrid();

  List<Grid.Column> getColumnList();

}
