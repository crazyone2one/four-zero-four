package cn.master.system.config;

import cn.master.system.handler.ScheduleManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : 11's papa
 * @since : 2026/5/22, 星期五
 **/
@Configuration
@EnableScheduling
public class ScheduleConfig {
    @Bean
    @ConditionalOnProperty(prefix = "quartz", value = "enabled", havingValue = "true")
    public ScheduleManager scheduleManager() {
        return new ScheduleManager();
    }
}
