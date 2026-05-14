package cn.master.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@Getter
@ConfigurationProperties(prefix = "malo.quartz")
public class QuartzProperties {
    @Setter
    private boolean enabled = false;
    @Setter
    private String schedulerName = "quartzScheduler";
    @Setter
    private String timeZone = "Asia/Shanghai";
    @Setter
    private Integer threadCount = 10;

    @Setter
    private Duration startupDelay = Duration.ofSeconds(60);
    @Setter
    private String groupName = "DEFAULT";

    private final Map<String, String> properties = new HashMap<>();


}
