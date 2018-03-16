package life.qbic.projectsStatsModule;

import com.vaadin.ui.HorizontalLayout;

/**
 * Created by spaethju on 12.04.17.
 */
public interface ProjectsStatsView {

    HorizontalLayout getProjectStats();

    void setNumberOfTotalProjects(int number);

    void setNumberOfOverdueProjects(int number);

    void setNumberOfUnregisteredProjects(int number);

    void setNumberOfInTimeProjects(int number);

}
