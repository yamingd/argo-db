//package com.argo.db.mysql.demo.mapper;
//
//import com.argo.db.mysql.demo.Person;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by yamingd on 9/16/15.
// */
//public class PersonMapperTest {
//
//    PersonMapper personMapper;
//
//    @Before
//    public void setUp() throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
//        personMapper = context.getBean(PersonMapper.class);
//        Assert.assertNotNull(personMapper);
//    }
//
//    @Test
//    public void testFind() throws Exception {
//        Person person = personMapper.find(null, 1);
//        Assert.assertNotNull(person);
//        Assert.assertNotNull(person.getId());
//        Assert.assertNotNull(person.getName());
//        System.out.println(person);
//    }
//
//    @Test
//    public void testFindInMaster() throws Exception {
//        Person person = personMapper.find(null, 1);
//        Assert.assertNotNull(person);
//        Assert.assertNotNull(person.getId());
//        Assert.assertNotNull(person.getName());
//        System.out.println(person);
//    }
//
//    @Test
//    public void testInsert() throws Exception {
//        Person person = new Person();
//        person.setName("abc 123");
//        personMapper.insert(null, person);
//        Assert.assertNotNull(person.getId());
//        System.out.println(person);
//    }
//
//    @Test
//    public void testInsertBatch() throws Exception {
//
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//        Person person = new Person();
//        person.setId(1);
//        person.setName("testUpdate");
//        boolean ret = personMapper.update(null, person);
//        Assert.assertTrue(ret);
//
//        Person person1 = personMapper.find(null, 1);
//        Assert.assertSame(person.getName(), person1.getName());
//    }
//
//    @Test
//    public void testUpdate1() throws Exception {
//        boolean ret = personMapper.update(null, "statusId=?, mobile=?, chsCode=?", "id=?", 1, "1234567891", "abc", 1143);
//        Assert.assertTrue(ret);
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//        Person person = new Person();
//        person.setId(1143);
//        person.setName("testDelete");
//        boolean ret = personMapper.delete(null, person);
//        Assert.assertTrue(ret);
//    }
//
//    @Test
//    public void testDeleteBy() throws Exception {
//        boolean ret = personMapper.deleteBy(null, 1144);
//        Assert.assertTrue(ret);
//    }
//
//    @Test
//    public void testDeleteBy1() throws Exception {
//        boolean ret = personMapper.deleteBy(null, "id=?", 1145);
//        Assert.assertTrue(ret);
//    }
//
//    @Test
//    public void testSelectRows() throws Exception {
//        List<Person> persons = personMapper.selectRows(null, new Integer[]{1139, 1121, 1122, 1123, 1047}, true);
//        Assert.assertEquals(5, persons.size());
//        for (int i = 0; i < persons.size(); i++) {
//            System.out.println(persons.get(i));
//        }
//    }
//
//    @Test
//    public void testSelectRowsTs() throws Exception {
//        long ts = System.currentTimeMillis();
//        for (int i = 0; i < 10 * 1000; i++) {
//            List<Person> persons = personMapper.selectRows(null, new Integer[]{1139, 1121, 1122, 1123, 1047}, true);
//        }
//        ts = System.currentTimeMillis() - ts;
//        System.out.println(ts + " ms ");
//    }
//
//    @Test
//    public void testSelectRowsInDb() throws Exception {
//        List<Person> persons = personMapper.selectRows(null, new Integer[]{1139, 1121, 1122, 1123, 1047}, false);
//        Assert.assertEquals(5, persons.size());
//        for (int i = 0; i < persons.size(); i++) {
//            System.out.println(persons.get(i));
//        }
//    }
//
//    @Test
//    public void testSelectPks() throws Exception {
//        List<Integer> pks = personMapper.selectPKs(null, " id < ?", null, new Object[]{1000});
//        System.out.println(pks);
//
//        pks = personMapper.selectPKs(null, " id desc", 100);
//        System.out.println(pks);
//    }
//
//    @Test
//    public void testSelectPksTs() throws Exception {
//        long ts = System.currentTimeMillis();
//        for (int i = 0; i < 10 * 1000; i++) {
//            List<Integer> pks = personMapper.selectPKs(null, " id < ?", "id desc", new Object[]{1000});
//        }
//        ts = System.currentTimeMillis() - ts;
//        System.out.println(ts + " ms ");
//    }
//
//    @Test
//    public void testSelectRows1() throws Exception {
//        List<Person> personList = personMapper.selectRows(null, "statusId desc", 100);
//        Assert.assertEquals(100, personList.size());
//        System.out.println(personList);
//    }
//
//    @Test
//    public void testSelectRows2() throws Exception {
//        List<Person> personList = personMapper.selectRows(null, "id > ?", "statusId desc", 100, new Object[]{10});
//        Assert.assertEquals(100, personList.size());
//        System.out.println(personList);
//    }
//
//    @Test
//    public void testSelectPks1() throws Exception {
//        List<Integer> personList = personMapper.selectPks(null, "id > ?", "statusId desc", 100, new Object[]{10});
//        Assert.assertEquals(100, personList.size());
//        System.out.println(personList);
//    }
//
//    @Test
//    public void testSelectPKs() throws Exception {
//        List<Integer> personList = personMapper.selectPKs(null, "id > ?", "id asc", new Object[]{10});
//        System.out.println(personList);
//    }
//
//    @Test
//    public void testSelectMap() throws Exception {
//        Map<String, Object> map = personMapper.selectMap(null, "statusId, count(1) as total", " statusId is not null and id > ?", "statusId", new Object[]{10});
//        System.out.println(map);
//    }
//
//    @Test
//    public void testSelectMap2() throws Exception {
//        Map<String, Object> map = personMapper.selectMap(null, "distinct statusId", " statusId is not null and id > ?", null, new Object[]{10});
//        System.out.println(map);
//    }
//
//    @Test
//    public void testCount() throws Exception {
//        int total = personMapper.count(null, "statusId is not null and id > ?", new Object[]{10});
//        System.out.println(total);
//    }
//}
