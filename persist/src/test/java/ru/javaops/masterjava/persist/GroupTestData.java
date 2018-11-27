package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

import java.util.List;

public class GroupTestData {
    public static Group TOPJAVA_06;
    public static Group TOPJAVA_07;
    public static Group TOPJAVA_08;
    public static Group MASTERJAVA_01;
    public static List<Group> FIST4_GROUPS;

    public static void init() {
        TOPJAVA_06 = new Group("topjava06", GroupType.FINISHED);
        TOPJAVA_07 = new Group("topjava07", GroupType.FINISHED);
        TOPJAVA_08 = new Group("topjava08", GroupType.CURRENT);
        MASTERJAVA_01 = new Group("masterjava01", GroupType.CURRENT);
        FIST4_GROUPS = ImmutableList.of(MASTERJAVA_01, TOPJAVA_06, TOPJAVA_07, TOPJAVA_08);
    }

    public static void setup() {
        GroupDao dao = DBIProvider.getDao(GroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIST4_GROUPS.forEach(dao::insert);
        });
    }
}
