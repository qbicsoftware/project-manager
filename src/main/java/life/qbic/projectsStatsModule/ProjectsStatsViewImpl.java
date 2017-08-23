package life.qbic.projectsStatsModule;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.VerticalLayout;
import life.qbic.NumberIndicator;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsViewImpl implements ProjectsStatsView {

    private VerticalLayout projectStatsLayout;
    private NumberIndicator totalProjectsNI, overdueProjectsNI;

    public ProjectsStatsViewImpl() {
        projectStatsLayout = new VerticalLayout();
        init();
    }

    public void init() {
        projectStatsLayout.removeAllComponents();
        projectStatsLayout.setWidth(33, Sizeable.Unit.PERCENTAGE);

        totalProjectsNI = new NumberIndicator();
        totalProjectsNI.setHeader("Projects");
        totalProjectsNI.setNumber(0);
        overdueProjectsNI = new NumberIndicator();
        overdueProjectsNI.setHeader("Overdue");
        overdueProjectsNI.setNumber(0);
        overdueProjectsNI.getNumber().setStyleName("overdue");
        projectStatsLayout.addComponents(totalProjectsNI, overdueProjectsNI);
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
