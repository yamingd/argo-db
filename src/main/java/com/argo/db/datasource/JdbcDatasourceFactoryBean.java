package com.argo.db.datasource;

import com.argo.db.JdbcConfig;
import com.argo.db.Roles;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by yaming_deng on 14-9-10.
 */
public class JdbcDatasourceFactoryBean implements FactoryBean<MySqlMSDataSource>, InitializingBean, DisposableBean {

    public static final String JDBC_YAML = "jdbc.yaml";
    private String confName;
    private String url;
    private String name;

    private MySqlMSDataSource dataSource;

    @Override
    public MySqlMSDataSource getObject() throws Exception {
        return dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return MySqlMSDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        JdbcConfig config = null;
        if (this.getConfName() == null){
            config = JdbcConfig.load(JDBC_YAML);
        }else{
            config = JdbcConfig.load(this.getConfName());
        }
        if (StringUtils.isNotBlank(this.url)){
            config.setUrl(this.url);
        }
        dataSource = new MySqlMSDataSource(config, Roles.MASTER);
        dataSource.init();
    }

    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void destroy() throws Exception {
        this.dataSource.close();
    }
}
