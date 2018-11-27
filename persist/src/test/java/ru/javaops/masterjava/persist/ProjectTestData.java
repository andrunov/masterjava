package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public class ProjectTestData {
    public static Project TOPJAVA;
    public static Project MASTERAVA;
    public static List<Project> FISTS2_PROJECTS;

    public static void init() {
        TOPJAVA = new Project("topjava");
        MASTERAVA = new Project("masterjava");
        FISTS2_PROJECTS = ImmutableList.of(MASTERAVA, TOPJAVA);
    }

    public static void setup() {
        ProjectDao dao = DBIProvider.getDao(ProjectDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FISTS2_PROJECTS.forEach(dao::insert);
        });
    }
}
