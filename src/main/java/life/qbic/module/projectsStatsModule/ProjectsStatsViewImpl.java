package life.qbic.module.projectsStatsModule;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import life.qbic.NumberIndicator;

/**
 * Created by spaethju on 12.04.17.
 */
public class ProjectsStatsViewImpl implements ProjectsStatsView {

  private HorizontalLayout statsLayout;
  private NumberIndicator totalProjectsNI, overdueProjectsNI, unregisteredProjectsNI, inTimeProjectsNI;

  public ProjectsStatsViewImpl() {
    statsLayout = new HorizontalLayout();
    init();
  }

  public void init() {

    statsLayout.removeAllComponents();

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
    statsLayout.addComponents(totalProjectsNI, inTimeProjectsNI, overdueProjectsNI,
        unregisteredProjectsNI);
    statsLayout.setSpacing(true);
    statsLayout.setMargin(new MarginInfo(false, true, true, true));
  }

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

  @Override
  public HorizontalLayout getStatsLayout() {
    return statsLayout;
  }
}
