package life.qbic.projectsStatsModule;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import life.qbic.NumberIndicator;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsViewImpl implements ProjectsStatsView {

    private HorizontalLayout projectStatsLayout;
    private VerticalLayout stats1Layout, stats2Layout;
    private NumberIndicator totalProjectsNI, overdueProjectsNI, unregisteredProjectsNI, inTimeProjectsNI;

    public ProjectsStatsViewImpl() {
        projectStatsLayout = new HorizontalLayout();
        stats2Layout = new VerticalLayout();
        stats1Layout = new VerticalLayout();
        init();
    }

    public void init() {
        projectStatsLayout.removeAllComponents();
        projectStatsLayout.setHeight("200px");

        stats1Layout.removeAllComponents();
        stats2Layout.removeAllComponents();

        totalProjectsNI = new NumberIndicator();
        totalProjectsNI.setHeader("All Projects");
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
        stats1Layout.addComponents(totalProjectsNI, inTimeProjectsNI);
        stats2Layout.addComponents(overdueProjectsNI, unregisteredProjectsNI);
        projectStatsLayout.addComponents(stats1Layout, stats2Layout);
        projectStatsLayout.setSizeFull();
        stats1Layout.setSpacing(true);
        stats2Layout.setSpacing(true);
        projectStatsLayout.setSpacing(true);
    }

    @Override
    public HorizontalLayout getProjectStats() { return projectStatsLayout; }

    @Override
    public void setNumberOfTotalProjects(int number) {
        totalProjectsNI.setNumber(number);
    }

    @Override
    public void setNumberOfOverdueProjects(int number) {
        overdueProjectsNI.setNumber(number);
    }

    @Override
    public void setNumberOfUnregisteredProjects(int number) {
        unregisteredProjectsNI.setNumber(number);
    }

    @Override
    public void setNumberOfInTimeProjects(int number) {
        inTimeProjectsNI.setNumber(number);
    }

}
