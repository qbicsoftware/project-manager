package life.qbic.projectsStatsModule;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.VerticalLayout;
import life.qbic.NumberIndicator;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsViewImpl implements ProjectsStatsView {

    VerticalLayout projectStatsLayout;
    NumberIndicator totalProjectsNI, overdueProjectsNI;

    public ProjectsStatsViewImpl() {
        this.projectStatsLayout = new VerticalLayout();
        init();
    }

    public void init() {
        projectStatsLayout.setWidth(33, Sizeable.Unit.PERCENTAGE);

        this.totalProjectsNI = new NumberIndicator();
        totalProjectsNI.setHeader("Total Projects");
        totalProjectsNI.setNumber(0);
        this.overdueProjectsNI = new NumberIndicator();
        overdueProjectsNI.setHeader("Overdue Projects");
        overdueProjectsNI.setNumber(0);
        projectStatsLayout.addComponent(totalProjectsNI);
        projectStatsLayout.addComponent(overdueProjectsNI);
    }

    @Override
    public VerticalLayout getProjectStats() {
        return this.projectStatsLayout;
    }

    @Override
    public void setNumberOfTotalProjects(double number) {
        totalProjectsNI.setNumber((int) number);
    }

    @Override
    public void setNumberOfOverdueProjects(double number) {
        overdueProjectsNI.setNumber((int) number);
    }
}
