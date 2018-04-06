package life.qbic.projectsStatsModule;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by spaethju on 12.04.17.
 */
public interface ProjectsStatsView {

  VerticalLayout getProjectStats();

  void setNumberOfTotalProjects(int number);

  void setNumberOfOverdueProjects(int number);

  void setNumberOfUnregisteredProjects(int number);

  void setNumberOfInTimeProjects(int number);

}
