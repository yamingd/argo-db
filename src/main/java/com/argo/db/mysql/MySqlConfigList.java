package com.argo.db.mysql;

import com.argo.yaml.YamlTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Created by yamingd on 9/10/15.
 */
public class MySqlConfigList {

    private static final String confName = "mysql.yaml";

    public static MySqlConfigList all;

    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        if (null != all){
            return;
        }

        all = YamlTemplate.load(MySqlConfigList.class, confName);
    }

    private List<MySqlConfig> multi;

    private List<MySqlMSConfig> ms;

    public List<MySqlConfig> getMulti() {
        return multi;
    }

    public void setMulti(List<MySqlConfig> multi) {
        this.multi = multi;
    }

    public List<MySqlMSConfig> getMs() {
        return ms;
    }

    public void setMs(List<MySqlMSConfig> ms) {
        this.ms = ms;
    }

}
