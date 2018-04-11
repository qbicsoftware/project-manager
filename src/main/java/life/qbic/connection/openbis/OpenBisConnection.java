package life.qbic.connection.openbis;

import com.vaadin.data.util.BeanItemContainer;
import java.util.Date;
import life.qbic.beans.ProjectBean;
import life.qbic.beans.ProjectToProjectBeanConverter;
import life.qbic.openbis.openbisclient.OpenBisClient;


/**
 * Created by sven1103 on 8/12/16.
 */
public class OpenBisConnection {

  private OpenBisClient openBisClient;

  private BeanItemContainer<ProjectBean> projectBeanBeanItemContainer = new BeanItemContainer<ProjectBean>(
      ProjectBean.class);

  public boolean initConnection(OpenBisClient openBisClient) {

    if (this.openBisClient != null) {
      this.openBisClient.logout();
    }
    try {
      this.openBisClient = openBisClient;
      this.openBisClient.login();
    } catch (Exception exp) {
      exp.printStackTrace();
      return false;
    }
    return true;
  }

  public String getSpaceOfProject(String projectCode) {
    if (this.openBisClient == null) {
      return null;
    }
    return openBisClient.getProjectByCode(projectCode).getSpaceCode();
  }

  public Date getProjectRegistrationDate(String projectCode) {
    if (this.openBisClient == null) {
      return null;
    }
    return openBisClient.getProjectByCode(projectCode).getRegistrationDetails()
        .getRegistrationDate();
  }

  public BeanItemContainer<ProjectBean> getListOfProjects() {
    if (this.openBisClient == null) {
      return null;
    }
    if (projectBeanBeanItemContainer.size() > 0) {
      projectBeanBeanItemContainer.removeAllItems();
    }
    openBisClient.listProjects().forEach(project -> {
      projectBeanBeanItemContainer.addBean(
          ProjectToProjectBeanConverter.convertToProjectBean(project));
    });

    return projectBeanBeanItemContainer;
  }

  public String getProjectDescription(String projectCode) {
    if (this.openBisClient == null || projectCode == null || projectCode.isEmpty()) {
      return "No description available.";
    }
    return this.openBisClient.getProjectByCode(projectCode).getDescription();
  }

}
