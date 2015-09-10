package com.argo.db;

import com.argo.yaml.YamlTemplate;

import java.io.IOException;

/**
 * User: yamingdeng
 * Date: 13-11-25
 * Time: 下午9:54
 */
public class JdbcConfig {

    private static final String confName = "jdbc.yaml";

    public static JdbcConfig instance = null;

    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        if (instance != null){
            return;
        }

        JdbcConfig.instance = load(confName);
    }

    /**
     * 加载配置信息
     * @throws IOException
     */
    public static JdbcConfig load(String confName) throws IOException {
        JdbcConfig config = YamlTemplate.load(JdbcConfig.class, confName);
        return config;
    }
}
