package com.argo.db;

import com.argo.db.mysql.MySQLConnectionHook;
import com.argo.yaml.YamlTemplate;
import com.jolbox.bonecp.BoneCPConfig;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yamingd on 9/10/15.
 */
public class BoneCPConfigBuilder {

    /**
     * 构造配置
     * @param confName
     * @return
     * @throws IOException
     */
    public static BoneCPConfig build(String confName) throws IOException {

        Map jdbcConfig = null;
        jdbcConfig = YamlTemplate.load(confName).getData();
        BoneCPConfig config = new BoneCPConfig();
        Field[] fields = BoneCPConfig.class.getDeclaredFields();
        Iterator<Object> itor = jdbcConfig.keySet().iterator();
        while (itor.hasNext()){
            String name = (String)itor.next();
            Object value = jdbcConfig.get(name);
            for (Field field: fields){
                if (field.getName().equalsIgnoreCase(name)){
                    try {
                        field.setAccessible(true);
                        field.set(config, value);
                    } catch (Exception e) {
                        // should never happen
                    }
                }

            }
        }

        config.setConnectionHook(new MySQLConnectionHook());
        config.setStatisticsEnabled(true);
        config.setDisableJMX(false);

        return config;

    }

    /**
     *
     * @param userName
     * @param password
     * @param jdbcConfig
     * @return
     * @throws IOException
     */
    public static BoneCPConfig build(String userName, String password, Map jdbcConfig) throws IOException {

        //Map jdbcConfig = null;
        //jdbcConfig = YamlTemplate.load(confName).getData();

        BoneCPConfig config = new BoneCPConfig();
        Field[] fields = BoneCPConfig.class.getDeclaredFields();
        Iterator<Object> itor = jdbcConfig.keySet().iterator();
        while (itor.hasNext()){
            String name = (String)itor.next();
            Object value = jdbcConfig.get(name);
            for (Field field: fields){
                if (field.getName().equalsIgnoreCase(name)){
                    try {
                        field.setAccessible(true);
                        field.set(config, value);
                    } catch (Exception e) {
                        // should never happen
                    }
                }

            }
        }

        config.setUsername(userName);
        config.setPassword(password);
        config.setConnectionHook(new MySQLConnectionHook());
        config.setStatisticsEnabled(true);
        config.setDisableJMX(false);

        return config;

    }

}
