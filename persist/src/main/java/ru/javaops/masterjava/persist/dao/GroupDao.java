package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    public Group insert(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertWitId(group);
        }
        return group;
    }

    @SqlQuery("SELECT nextval('group_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE group_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO groups (group_name, group_type) VALUES (:groupName, CAST(:groupType AS group_type)) ")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, group_name, group_type) VALUES (:id, :groupName, CAST(:groupType AS group_type))")
    abstract void insertWitId(@BindBean Group group);

    @SqlQuery("SELECT * FROM groups ORDER BY group_name LIMIT :it")
    public abstract List<Group> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE groups CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO groups (id, group_name, group_type) VALUES (:id, :groupName, CAST(:groupType AS group_type))" +
            "ON CONFLICT DO NOTHING")
//            "ON CONFLICT (email) DO UPDATE SET full_name=:fullName, flag=CAST(:flag AS USER_FLAG)")
    public abstract int[] insertBatch(@BindBean List<Group> groups, @BatchChunkSize int chunkSize);

}
