package com.argo.db.datasource;

import com.argo.db.BoneCPConfigBuilder;
import com.argo.db.mysql.MysqlConstants;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jolbox.bonecp.BoneCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.List;

/**
 * 
 * 分布式数据源工厂Bean.
 * 
 * Factory --> RoutingDataSource --> DataSource
 *
 * @author yaming_deng
 * 2013-1-24
 */
public class MySqlMSDataSourceFactoryBean implements FactoryBean<MySqlMSRoutingDataSource>, InitializingBean, DisposableBean  {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private MySqlMSRoutingDataSource msDataSource;

    private List<String> servers;
    private String name;
    private String role;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public MySqlMSRoutingDataSource getObject() throws Exception {
		return msDataSource;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return MySqlMSRoutingDataSource.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(this.getName());
        Preconditions.checkNotNull(this.getRole());
        Preconditions.checkNotNull(this.getServers());
        Preconditions.checkArgument(this.getServers().size() > 0, "servers is empty");

        //1. common config
        BoneCPConfig jdbcConfig = BoneCPConfigBuilder.build("jdbc.yaml");

		this.msDataSource = new MySqlMSRoutingDataSource();

		//M-S数据源
        List<DataSource> sourceList = Lists.newArrayList();
        for (String url : this.servers) {
            jdbcConfig.setJdbcUrl(String.format(MysqlConstants.DRIVER_URL_MYSQL, url));
            MySqlMSDataSource master = new MySqlMSDataSource(jdbcConfig, role);
            master.setDriverClass(MysqlConstants.DRIVER_MYSQL);
            master.setRole(role);
            sourceList.add(master);
        }

        this.msDataSource.setName(this.name);
        this.msDataSource.setRole(this.role);
        this.msDataSource.setTargetDataSources(sourceList);

        logger.info("create datasource. name={}, role={}, servers={}", this.getName(), this.getRole(), this.getServers());
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		this.msDataSource.destroy();
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
