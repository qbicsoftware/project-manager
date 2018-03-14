package life.qbic.projectsStatsModule;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.VerticalLayout;
import life.qbic.NumberIndicator;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsViewImpl implements ProjectsStatsView {

    private VerticalLayout projectStatsLayout;
    private NumberIndicator totalProjectsNI, overdueProjectsNI, unregisteredProjectsNI, inTimeProjectsNI;

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
        unregisteredProjectsNI = new NumberIndicator();
        unregisteredProjectsNI.setHeader("Unregistered");
        unregisteredProjectsNI.setNumber(0);
        unregisteredProjectsNI.getNumber().setStyleName("unregistered");
        inTimeProjectsNI = new NumberIndicator();
        inTimeProjectsNI.setHeader("In Time");
        inTimeProjectsNI.setNumber(0);
        inTimeProjectsNI.getNumber().setStyleName("intime");
        projectStatsLayout.addComponents(totalProjectsNI, overdueProjectsNI, unregisteredProjectsNI, inTimeProjectsNI);
    }

    @Override
    public VerticalLayout getProjectStats() {
        return this.projectStatsLayout;
    }

    @Override
    public void setNumberOfTotalProjects(int number) {
        totalProjectsNI.setNumber(number);
    }

    @Override
    public void setNumberOfOverdueProjects(int number) {
        overdueProjectsNI.setNumber( number);
    }

    @Override
    public void setNumberOfUnregisteredProjects(int number) {
        unregisteredProjectsNI.setNumber( number);
    }

    @Override
    public void setNumberOfInTimeProjects(int number) {
        inTimeProjectsNI.setNumber( number);
    }

}
