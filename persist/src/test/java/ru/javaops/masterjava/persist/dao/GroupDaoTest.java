package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.GroupTestData;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        GroupTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        GroupTestData.setup();
    }

    @Test
    public void getWithLimit() {
        List<Group> users = dao.getWithLimit(5);
        Assert.assertEquals(GroupTestData.FIST4_GROUPS, users);
    }

    @Test
    public void insertBatch() throws Exception {
        dao.clean();
        dao.insertBatch(GroupTestData.FIST4_GROUPS, 3);
        Assert.assertEquals(4, dao.getWithLimit(100).size());
    }


    @Test
    public void getSeqAndSkip() throws Exception {
        int seq1 = dao.getSeqAndSkip(5);
        int seq2 = dao.getSeqAndSkip(1);
        Assert.assertEquals(5, seq2 - seq1);
    }
}
