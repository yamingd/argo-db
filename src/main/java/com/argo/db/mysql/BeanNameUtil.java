package com.argo.db.mysql;

/**
 * Created by yamingd on 9/16/15.
 */
public class BeanNameUtil {

    public static final String DS = "DS_";

    /**
     *
     * @param serverName serverName
     * @return String
     */
    public static String getDsBeanName(String serverName){
        String beanName = DS + serverName;
        return beanName;
    }

    /**
     *
     * @param serverName serverName
     * @param role role
     * @return String
     */
    public static String getDsBeanName(String serverName, String role){
        String beanName = DS + serverName + "_" + role;
        return beanName;
    }

    /**
     *
     * @param serverName serverName
     * @param role role
     * @return String
     */
    public static String getJtBeanName(String serverName, String role){
        String beanName = DS + serverName + "_" + role + "Jt";
        return beanName;
    }

    /**
     *
     * @param serverName serverName
     * @return String
     */
    public static String getJtBeanName(String serverName){
        String beanName = DS + serverName + "Jt";
        return beanName;
    }

}
