package life.qbic.beans;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;

/**
 * Created by sven on 12/18/16.
 */
public class ProjectToProjectBeanConverter {

    public static ProjectBean convertToProjectBean(Project project) {
        ProjectBean newProject = new ProjectBean();
        newProject.setId(project.getPermId() != null ? project.getPermId() : "");
        newProject.setSpace(project.getSpaceCode() != null ? project.getSpaceCode() : "");
        newProject.setCode(project.getCode() != null ? project.getCode() : "");
        newProject.setDescription(project.getDescription() != null ? project.getDescription() : "No description available");

        return newProject;
    }

}
