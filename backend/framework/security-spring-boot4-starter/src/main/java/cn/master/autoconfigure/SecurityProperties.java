package cn.master.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "malo.security")
public class SecurityProperties {
    /*** 是否启用自定义安全配置*/
    private boolean enabled = false;

    /*** 忽略认证的 URL 路径*/
    private List<String> ignorePaths = new ArrayList<>();

    /*** access token有效期（秒）*/
    private long accessExpire = 7200;

    /*** refresh token有效期（秒）*/
    private long refreshExpire = 604800;

    /*** JWT 密钥 (示例) */
    private String secretKey = "a3f8b2c1d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1";

    private String tokenHeader = "Authorization";
}
