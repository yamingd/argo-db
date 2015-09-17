package com.argo.db.beans;

import com.argo.db.MapperConfig;
import com.argo.db.Roles;
import com.argo.db.datasource.MySqlMSDataSourceFactoryBean;
import com.argo.db.mysql.BeanNameUtil;
import com.argo.db.mysql.MySqlConfigList;
import com.argo.db.mysql.MySqlMSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-22.
 */
public class MySqlMSDataSourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            throw new IllegalStateException(
                    "CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
        }

        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

        try {

            MySqlConfigList.load();
            MapperConfig.load();

            if (MySqlConfigList.all.getMs() == null || MySqlConfigList.all.getMs().size() == 0){
                throw new Exception("can't load mysql.yaml or mysql.yaml is empty.");
            }

            List<MySqlMSConfig> servers = MySqlConfigList.all.getMs();
            for (MySqlMSConfig entry : servers){
                this.postAddDataSource(dlbf, entry.getName(), Roles.MASTER, entry.getMaster());
                this.postAddDataSource(dlbf, entry.getName(), Roles.SLAVE, entry.getSlave());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 每个(ip/database, role)为一个单元
     * serverName 为server serverName
     * @param dlbf
     * @param serverName
     * @param role
     * @param servers
     */
    private void postAddDataSource(DefaultListableBeanFactory dlbf, String serverName, String role, List<String> servers) {
        String beanName = BeanNameUtil.getDsBeanName(serverName, role);

        logger.info("@@@postAddDataSource, serverName=" + serverName + ", role=" + role);

        //datasource
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MySqlMSDataSourceFactoryBean.class.getName());
        builder.addPropertyValue("name", serverName);
        builder.addPropertyValue("role", role);
        builder.addPropertyValue("servers", servers);
        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        //transaction
        if (Roles.MASTER.equalsIgnoreCase(role)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
            builder.addConstructorArgReference(beanName);
            dlbf.registerBeanDefinition(serverName + "Tx", builder.getBeanDefinition());
        }

        //jdbc template
        builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Jt", builder.getBeanDefinition());
    }

}
