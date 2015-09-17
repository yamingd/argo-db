package com.argo.db.mysql;

import com.argo.db.mysql.demo.mapper.PersonMapper;
import junit.framework.Assert;
import org.junit.Before;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yamingd on 9/10/15.
 */
public class MySqlDataSourceTest {

    PersonMapper personMapper;

    @Before
    public void setUp() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
        personMapper = context.getBean(PersonMapper.class);
        Assert.assertNotNull(personMapper);
    }

}
