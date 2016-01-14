package com.argo.db.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.argo.db.JdbcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Master-Slave数据源
 *
 * @author yaming_deng
 * 2013-1-24
 */
public class MySqlMSDataSource extends DruidDataSource{

	/**
	 *
	 */
	private static final long serialVersionUID = 8914094842865950071L;

	private static final Logger logger = LoggerFactory.getLogger(MySqlMSDataSource.class);

	/**
	 * @param config
	 */
	public MySqlMSDataSource(JdbcConfig config, String role) {
		this.setUrl(config.getUrl());
		this.setUsername(config.getUsername());
		this.setPassword(config.getPassword());
        this.role = role;
	}

    private String role = "";

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
