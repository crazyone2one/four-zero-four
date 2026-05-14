package cn.master.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "malo.redis")
public class RedisProperties {
    private boolean enabled = false;
    private String host = "localhost";
    private int port = 6379;
    private String password;
}
