package life.qbic.beans;


import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project;

/**
 * Created by sven on 12/18/16.
 */
public class ProjectToProjectBeanConverter {

  public static ProjectBean convertToProjectBean(Project project) {
    ProjectBean newProject = new ProjectBean();
    newProject.setId(project.getPermId() != null ? project.getPermId().toString() : "");
    newProject.setSpace(project.getCode() != null ? project.getCode() : "");
    newProject.setCode(project.getCode() != null ? project.getCode() : "");
    newProject.setDescription(
        project.getDescription() != null ? project.getDescription() : "No description available");

    return newProject;
  }

}
