package com.argo.db;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * Created by yaming_deng on 14-7-28.
 */
@Component
public class MasterSlaveJdbcTemplate implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    public static final String ROLE_SLAVE = "slave";
    public static final String ROLE_MASTER = "master";

    /**
     *
     * @param serverName
     * @return
     */
    public JdbcTemplate slave(String serverName){
        String beanName = "DS_" + serverName + "_" + ROLE_SLAVE + "Jt";
        JdbcTemplate jdbcTemplate = this.applicationContext.getBean(beanName, JdbcTemplate.class);
        return jdbcTemplate;
    }

    /**
     *
     * @param serverName
     * @return
     */
    public JdbcTemplate master(String serverName){
        String beanName = "DS_" + serverName + "_" + ROLE_MASTER + "Jt";
        JdbcTemplate jdbcTemplate = this.applicationContext.getBean(beanName, JdbcTemplate.class);
        return jdbcTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
