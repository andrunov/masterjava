package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityTestData {
    public static City SPB;
    public static City MSK;
    public static City KIV;
    public static City MNSK;
    public static List<City> FIST4_CITIES;

    public static void init() {
        SPB = new City("Санкт-Петербург", "spb");
        MSK = new City("Москва", "msk");
        KIV = new City("Киев", "kiv");
        MNSK = new City("Минск", "mnsk");
        FIST4_CITIES = ImmutableList.of(KIV, MNSK, MSK, SPB);
    }

    public static void setup() {
        CityDao dao = DBIProvider.getDao(CityDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            FIST4_CITIES.forEach(dao::insert);
        });
    }
}
