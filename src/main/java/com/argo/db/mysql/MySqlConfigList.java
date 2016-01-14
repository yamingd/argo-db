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
     * @throws IOException IOException
     */
    public synchronized static void load() throws IOException {
        if (null != all){
            return;
        }

        all = YamlTemplate.load(MySqlConfigList.class, confName);
    }

    private boolean printsql;
    private boolean memcache;

    private List<MySqlConfig> multi;

    private List<MySqlMSConfig> ms;

    private List<MySqlShardConfig> shard;

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

    public List<MySqlShardConfig> getShard() {
        return shard;
    }

    public void setShard(List<MySqlShardConfig> shard) {
        this.shard = shard;
    }

    public boolean isPrintsql() {
        return printsql;
    }

    public void setPrintsql(boolean printsql) {
        this.printsql = printsql;
    }

    public boolean isMSEnabled(){
        return ms != null && ms.size() > 0;
    }

    public boolean isShardEnabled(){
        return shard != null && shard.size() > 0;
    }

    public boolean isMultiEnabled(){
        return multi != null && multi.size() > 0;
    }

    public boolean isMemcache() {
        return memcache;
    }

    public void setMemcache(boolean memcache) {
        this.memcache = memcache;
    }
}
