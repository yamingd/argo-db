package com.argo.db.datasource;

import com.argo.db.BoneCPConfigBuilder;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by yaming_deng on 14-9-10.
 */
public class JdbcDatasourceFactoryBean implements FactoryBean<BoneCPDataSource>, InitializingBean, DisposableBean {

    public static final String JDBC_YAML = "jdbc.yaml";
    private String confName;
    private String url;
    private String name;

    private BoneCPDataSource dataSource;

    @Override
    public BoneCPDataSource getObject() throws Exception {
        return dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return BoneCPDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        BoneCPConfig config = null;
        if (this.getConfName() == null){
            config = BoneCPConfigBuilder.build(JDBC_YAML);
        }else{
            config = BoneCPConfigBuilder.build(this.getConfName());
        }
        if (StringUtils.isNotBlank(this.url)){
            config.setJdbcUrl(this.url);
        }
        dataSource = new BoneCPDataSource(config);
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
