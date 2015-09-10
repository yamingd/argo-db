package com.argo.db.beans;

import com.argo.db.MasterSlaveJdbcTemplate;
import com.argo.db.datasource.MySqlMSDataSourceFactoryBean;
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

            if (MySqlConfigList.all.getMs() == null || MySqlConfigList.all.getMs().size() == 0){
                throw new Exception("can't load mysql.yaml or mysql.yaml is empty.");
            }

            List<MySqlMSConfig> servers = MySqlConfigList.all.getMs();
            for (MySqlMSConfig entry : servers){
                this.postAddDataSource(dlbf, entry.getName(), MasterSlaveJdbcTemplate.ROLE_MASTER, entry.getMaster());
                this.postAddDataSource(dlbf, entry.getName(), MasterSlaveJdbcTemplate.ROLE_SLAVE, entry.getSlave());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postAddDataSource(DefaultListableBeanFactory dlbf, String name, String role, List<String> servers) {
        String beanName = "DS_" + name + "_" + role;

        logger.info("@@@postAddDataSource, name=" + name + ", role=" + role);

        //datasource
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MySqlMSDataSourceFactoryBean.class.getName());
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("role", role);
        builder.addPropertyValue("servers", servers);
        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        //transaction
        if (MasterSlaveJdbcTemplate.ROLE_MASTER.equalsIgnoreCase(role)) {
            builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
            builder.addConstructorArgReference(beanName);
            dlbf.registerBeanDefinition(name + "Tx", builder.getBeanDefinition());
        }

        //jdbc template
        builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Jt", builder.getBeanDefinition());
    }

}
