package life.qbic.projectsStatsModule;

import com.vaadin.ui.VerticalLayout;

/**
 * Created by spaethju on 12.04.17.
 */
public interface ProjectsStatsView {

    VerticalLayout getProjectStats();

    void setNumberOfTotalProjects(double number);

    void setNumberOfOverdueProjects(double number);

}
