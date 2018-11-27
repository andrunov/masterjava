package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    public Project insert(Project project) {
        if (project.isNew()) {
            int id = insertGeneratedId(project);
            project.setId(id);
        } else {
            insertWitId(project);
        }
        return project;
    }

    @SqlQuery("SELECT nextval('project_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE project_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO projects (project_name) VALUES (:projectName)")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (id, project_name) VALUES (:id, :projectName)")
    abstract void insertWitId(@BindBean Project project);

    @SqlQuery("SELECT * FROM projects ORDER BY project_name LIMIT :it")
    public abstract List<Project> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE projects CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO projects (id, project_name) VALUES (:id, :projectName)" +
            "ON CONFLICT DO NOTHING")
//            "ON CONFLICT (email) DO UPDATE SET full_name=:fullName, flag=CAST(:flag AS USER_FLAG)")
    public abstract int[] insertBatch(@BindBean List<Project> projects, @BatchChunkSize int chunkSize);

}
