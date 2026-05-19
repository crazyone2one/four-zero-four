package cn.master.security.handler;

import cn.master.result.ResultHolder;
import cn.master.security.jwt.JwtTokenProvider;
import cn.master.util.JsonUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtTokenProvider tokenProvider;

    public CustomLogoutSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, @NonNull HttpServletResponse response, @Nullable Authentication authentication) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = tokenProvider.parse(token);
                long expire = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
                if (expire > 0) {
                    tokenProvider.addBlacklist(token, expire);
                    tokenProvider.removeAccessToken(token);
                }
            } catch (Exception e) {
                // Token 解析失败，忽略
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        ResultHolder result = ResultHolder.success("logout success");
        response.getWriter().write(JsonUtils.toJSONString(result));
    }
}
