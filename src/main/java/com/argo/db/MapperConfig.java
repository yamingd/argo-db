package com.argo.db;

import com.argo.yaml.YamlTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * User: yamingdeng
 * Date: 13-11-25
 * Time: 下午9:54
 */
public class MapperConfig {

    private static final String confName = "mapper.yaml";

    public static MapperConfig instance = null;

    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        if (instance != null){
            return;
        }

        MapperConfig.instance = load(confName);
    }

    /**
     * 加载配置信息
     * @throws IOException
     */
    public static MapperConfig load(String confName) throws IOException {
        MapperConfig config = YamlTemplate.load(MapperConfig.class, confName);
        return config;
    }

    private Map<String, String> ms;

    public Map<String, String> getMs() {
        return ms;
    }

    public void setMs(Map<String, String> ms) {
        this.ms = ms;
    }

    /**
     *
     * @param table
     * @return String
     */
    public static String getServer(String table){
        return instance.getMs().get(table);
    }
}
