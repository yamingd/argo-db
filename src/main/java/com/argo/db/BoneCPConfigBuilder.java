//package com.argo.db;
//
//import com.argo.db.mysql.MySQLConnectionHook;
//import com.argo.yaml.YamlTemplate;
//import com.jolbox.bonecp.BoneCPConfig;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.Map;
//
///**
// * Created by yamingd on 9/10/15.
// */
//public class BoneCPConfigBuilder {
//
//    /**
//     * 构造配置
//     * @param confName
//     * @return BoneCPConfig
//     * @throws IOException
//     */
//    public static BoneCPConfig build(String confName) throws IOException {
//
//        BoneCPConfig config = YamlTemplate.load(BoneCPConfig.class, confName);
//        config.setConnectionHook(new MySQLConnectionHook());
//        config.setStatisticsEnabled(true);
//        config.setDisableJMX(false);
//
//        return config;
//
//    }
//
//    /**
//     *
//     * @param userName
//     * @param password
//     * @param jdbcConfig
//     * @return BoneCPConfig
//     * @throws IOException
//     */
//    public static BoneCPConfig build(String userName, String password, Map jdbcConfig) throws IOException {
//
//        //Map jdbcConfig = null;
//        //jdbcConfig = YamlTemplate.load(confName).getData();
//
//        BoneCPConfig config = new BoneCPConfig();
//        Field[] fields = BoneCPConfig.class.getDeclaredFields();
//
//        for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];
//            Object value = jdbcConfig.get(field.getName());
//            if (null != value){
//                try {
//                    field.setAccessible(true);
//                    field.set(config, value);
//                } catch (Exception e) {
//                    // should never happen
//                }
//            }
//        }
//
//        config.setUsername(userName);
//        config.setPassword(password);
//        config.setConnectionHook(new MySQLConnectionHook());
//        config.setStatisticsEnabled(true);
//        config.setDisableJMX(false);
//
//        return config;
//
//    }
//
//}
