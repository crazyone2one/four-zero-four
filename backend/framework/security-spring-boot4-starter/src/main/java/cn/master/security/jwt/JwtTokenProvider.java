package cn.master.security.jwt;

import cn.master.autoconfigure.SecurityProperties;
import cn.master.security.dto.LoginResultDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class JwtTokenProvider {
    private final SecretKey key;
    private final SecurityProperties properties;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "LOGIN:REFRESH:";
    private static final String BLACKLIST_PREFIX = "LOGIN:BLACKLIST:";
    private static final String ACCESS_TOKEN_PREFIX = "LOGIN:ACCESS:";

    public JwtTokenProvider(SecurityProperties properties, StringRedisTemplate stringRedisTemplate) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.getSecretKey()));
        this.properties = properties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public LoginResultDTO generateToken(String username) {
        String accessToken = Jwts.builder()
                .subject(username)
                // .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + properties.getAccessExpire() * 1000))
                .signWith(key)
                .compact();
        String refreshToken = UUID.randomUUID().toString();
        String refreshKey = REFRESH_TOKEN_PREFIX + refreshToken;
        stringRedisTemplate.opsForValue().set(
                refreshKey,
                username,
                properties.getRefreshExpire(),
                TimeUnit.SECONDS);
        return new LoginResultDTO(accessToken, refreshToken);
    }

    public boolean isBlacklist(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    public LoginResultDTO refreshAccessToken(String refreshToken) {
        String username = stringRedisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
        if (username == null) {
            return null;
        }
        // 删除旧的 refresh token
        stringRedisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
        // 生成新的 access token 和 refresh token
        return generateToken(username);
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void addBlacklist(String token, long expireSeconds) {
        stringRedisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "1",
                expireSeconds,
                TimeUnit.SECONDS);
    }

    public void removeAccessToken(String token) {
        stringRedisTemplate.delete(ACCESS_TOKEN_PREFIX + token);
    }
}
