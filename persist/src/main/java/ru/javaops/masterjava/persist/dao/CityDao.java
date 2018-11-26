package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    public City insert(City city) {
        if (city.isNew()) {
            int id = insertGeneratedId(city);
            city.setId(id);
        } else {
            insertWitId(city);
        }
        return city;
    }

    @SqlQuery("SELECT nextval('city_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE city_seq RESTART WITH " + (id + step)));
        return id;
    }

    @SqlUpdate("INSERT INTO cities (city_name, city_code) VALUES (:cityName, :cityCode)")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean City city);

    @SqlUpdate("INSERT INTO cities (id, city_name, city_code) VALUES (:id, :cityName, cityCode)")
    abstract void insertWitId(@BindBean City city);

    @SqlQuery("SELECT * FROM cities ORDER BY city_name LIMIT :it")
    public abstract List<City> getWithLimit(@Bind int limit);

    @SqlUpdate("TRUNCATE cities CASCADE")
    @Override
    public abstract void clean();

    @SqlBatch("INSERT INTO cities (id, city_name, city_code) VALUES (:id, :cityName, :cityCode)" +
            "ON CONFLICT DO NOTHING")
//            "ON CONFLICT (email) DO UPDATE SET full_name=:fullName, flag=CAST(:flag AS USER_FLAG)")
    public abstract int[] insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);

}
