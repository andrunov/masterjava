package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.ProjectTestData;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    public ProjectDaoTest() {
        super(ProjectDao.class);
    }


    @BeforeClass
    public static void init() {
        ProjectTestData.init();
    }

    @Before
    public void setup() {
        ProjectTestData.setup();
    }

    @Test
    public void getWithLimit() {
        List<Project> projects = dao.getWithLimit(4);
        Assert.assertEquals(ProjectTestData.FISTS2_PROJECTS, projects);
    }

    @Test
    public void insertBatch() throws Exception {
        dao.clean();
        dao.insertBatch(ProjectTestData.FISTS2_PROJECTS, 3);
        Assert.assertEquals(2, dao.getWithLimit(100).size());
    }

    @Test
    public void getSeqAndSkip() throws Exception {
        int seq1 = dao.getSeqAndSkip(4);
        int seq2 = dao.getSeqAndSkip(1);
        Assert.assertEquals(4, seq2 - seq1);
    }
}
