package cn.master.autoconfigure;

import cn.master.SchedulerStarter;
import cn.master.quartz.QuartzInstanceIdGenerator;
import cn.master.quartz.service.QuartzManageService;
import cn.master.quartz.util.QuartzBeanFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.quartz.autoconfigure.QuartzDataSource;
import org.springframework.boot.quartz.autoconfigure.QuartzTransactionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@Configuration
@ConditionalOnClass(SchedulerFactoryBean.class)
@EnableConfigurationProperties(QuartzProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ConditionalOnProperty(prefix = "malo.quartz", name = "enabled", havingValue = "true", matchIfMissing = true)
public class QuartzAutoConfiguration {
    private final ObjectProvider<DataSource> dataSourceProvider;
    private final PlatformTransactionManager txManager;
    private final QuartzProperties properties;

    public QuartzAutoConfiguration(ObjectProvider<DataSource> dataSource,
                                   @QuartzDataSource ObjectProvider<@NonNull DataSource> quartzDataSource,
                                   ObjectProvider<@NonNull PlatformTransactionManager> transactionManager,
                                   @QuartzTransactionManager ObjectProvider<@NonNull PlatformTransactionManager> quartzTransactionManager,
                                   QuartzProperties properties) {
        this.dataSourceProvider = getDataSourceProvider(dataSource, quartzDataSource);
        this.txManager = getTransactionManager(transactionManager, quartzTransactionManager);
        this.properties = properties;
    }

    private ObjectProvider<DataSource> getDataSourceProvider(ObjectProvider<DataSource> dataSource, ObjectProvider<DataSource> quartzDataSource) {
        DataSource dataSourceIfAvailable = quartzDataSource.getIfAvailable();
        return (dataSourceIfAvailable != null) ? quartzDataSource : dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerStarter schedulerStarter() {
        return new SchedulerStarter();
    }

    @Bean
    @ConditionalOnMissingBean
    public QuartzBeanFactory quartzBeanFactory() {
        return new QuartzBeanFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public QuartzManageService quartzManageService() {
        return new QuartzManageService();
    }

    @Bean
    public TimeZone quartzTimeZone() {
        return TimeZone.getTimeZone(properties.getTimeZone());
    }

    @Bean
    @ConditionalOnClass(DataSource.class)
    public SchedulerFactoryBean clusterSchedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource != null) {
            schedulerFactoryBean.setDataSource(dataSource);
        }
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
        Properties props = getProperties();
        // 外部传入的属性优先级更高
        Map<String, String> properties = this.properties.getProperties();
        if (!CollectionUtils.isEmpty(properties)) {
            props.putAll(properties);
        }

        schedulerFactoryBean.setQuartzProperties(props);
        if (this.properties.getSchedulerName() != null) {
            schedulerFactoryBean.setBeanName(this.properties.getSchedulerName());
        }
        if (txManager != null) {
            schedulerFactoryBean.setTransactionManager(txManager);
        }
        return schedulerFactoryBean;
    }

    private @NonNull Properties getProperties() {
        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", properties.getSchedulerName());
        props.put("org.quartz.scheduler.instanceId", "AUTO"); // 集群下的instanceId 必须唯一
        props.put("org.quartz.scheduler.instanceIdGenerator.class", QuartzInstanceIdGenerator.class.getName());// instanceId 生成的方式
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        props.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        props.put("org.quartz.jobStore.isClustered", "true");
        props.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        // thread count
        String threadCount = this.properties.getThreadCount().toString();
        props.put("org.quartz.threadPool.threadCount", threadCount);
        props.put("org.quartz.threadPool.threadPriority", "5");
        props.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        return props;
    }

    private DataSource getDataSource(DataSource dataSource,
                                     ObjectProvider<@NonNull DataSource> quartzDataSource) {
        DataSource dataSourceIfAvailable = quartzDataSource.getIfAvailable();
        return (dataSourceIfAvailable != null) ? dataSourceIfAvailable : dataSource;
    }

    private PlatformTransactionManager getTransactionManager(
            ObjectProvider<@NonNull PlatformTransactionManager> transactionManager,
            ObjectProvider<@NonNull PlatformTransactionManager> quartzTransactionManager) {
        PlatformTransactionManager transactionManagerIfAvailable = quartzTransactionManager.getIfAvailable();
        return (transactionManagerIfAvailable != null) ? transactionManagerIfAvailable : transactionManager.getIfUnique();
    }
}
