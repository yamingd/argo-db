package com.argo.db.beans;

import com.argo.db.datasource.JdbcDatasourceFactoryBean;
import com.argo.db.mysql.MySqlConfig;
import com.argo.db.mysql.MySqlConfigList;
import com.argo.db.mysql.MysqlConstants;
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
public class MySqlDataSourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

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

            if (MySqlConfigList.all.getMulti() == null || MySqlConfigList.all.getMulti() .size() == 0){
                throw new Exception("can't load mysql.yaml or mysql.yaml is empty.");
            }

            List<MySqlConfig> servers = MySqlConfigList.all.getMulti();
            for (MySqlConfig server : servers){
                this.postAddDataSource(dlbf, server);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postAddDataSource(DefaultListableBeanFactory dlbf, MySqlConfig server) {
        String beanName = "DS_" + server.getName();

        logger.info("@@@postAddDataSource, dbid={}", beanName);

        //datasource
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcDatasourceFactoryBean.class.getName());
        builder.addPropertyValue("name", server.getName());
        builder.addPropertyValue("url", String.format(MysqlConstants.DRIVER_URL_MYSQL, server.getUrl()));
        dlbf.registerBeanDefinition(beanName, builder.getBeanDefinition());

        //transaction
        builder = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Tx", builder.getBeanDefinition());

        //jdbc template
        builder = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class.getName());
        builder.addConstructorArgReference(beanName);
        dlbf.registerBeanDefinition(beanName + "Jt", builder.getBeanDefinition());
    }

}
