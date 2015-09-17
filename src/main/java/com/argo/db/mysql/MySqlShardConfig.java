package com.argo.db.mysql;

/**
 * Created by yamingd on 9/10/15.
 */
public class MySqlShardConfig {

    private String name;
    private String url;
    private String dbns;

    private String idPrefix;
    private int startId;
    private int endId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbns() {
        return dbns;
    }

    public void setDbns(String dbns) {
        this.dbns = dbns;
        String[] tmp = dbns.split(",");
        this.idPrefix = tmp[0].trim();
        this.startId = Integer.parseInt(tmp[1].trim());
        this.endId = Integer.parseInt(tmp[2].trim());
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }
}
