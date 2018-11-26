package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.CityTestData;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

public class CityDaoTest extends AbstractDaoTest<CityDao>{

    public CityDaoTest() {
        super(CityDao.class);
    }

    @BeforeClass
    public static void init() {
        CityTestData.init();
    }

    @Before
    public void setup() {
        CityTestData.setup();
    }

    @Test
    public void getWithLimit() {
        List<City> cities = dao.getWithLimit(4);
        Assert.assertEquals(CityTestData.FIST4_CITIES, cities);
    }


    @Test
    public void insertBatch() throws Exception {
       dao.clean();
       dao.insertBatch(CityTestData.FIST4_CITIES, 3);
        Assert.assertEquals(4, dao.getWithLimit(100).size());
    }

    @Test
    public void getSeqAndSkip() throws Exception {
        int seq1 = dao.getSeqAndSkip(4);
        int seq2 = dao.getSeqAndSkip(1);
        Assert.assertEquals(4, seq2 - seq1);
    }

}
