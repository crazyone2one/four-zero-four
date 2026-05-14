package cn.master.security.exception;

import cn.master.result.HttpResultCode;
import cn.master.result.ResultHolder;
import cn.master.util.JsonUtils;
import cn.master.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ResultHolder error = ResultHolder.error(HttpResultCode.UNAUTHORIZED.getCode(),
                HttpResultCode.UNAUTHORIZED.getMessage() + "[" + authException.getMessage() + "]",
                authException.getMessage());
        LogUtils.warn("访问 URL({}): Unauthorized", request.getRequestURI(), authException);
        response.getWriter().write(JsonUtils.toJSONString(error));
    }
}
