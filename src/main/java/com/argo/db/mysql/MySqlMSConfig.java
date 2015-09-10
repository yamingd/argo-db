package com.argo.db.mysql;

import java.util.List;

/**
 * Created by yamingd on 9/10/15.
 */
public class MySqlMSConfig {

    private String name;
    private List<String> master;
    private List<String> slave;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMaster() {
        return master;
    }

    public void setMaster(List<String> master) {
        this.master = master;
    }

    public List<String> getSlave() {
        return slave;
    }

    public void setSlave(List<String> slave) {
        this.slave = slave;
    }

}
