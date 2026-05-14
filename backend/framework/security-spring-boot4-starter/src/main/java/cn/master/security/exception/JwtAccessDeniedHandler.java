package cn.master.security.exception;

import cn.master.result.HttpResultCode;
import cn.master.result.ResultHolder;
import cn.master.util.JsonUtils;
import cn.master.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(@NonNull HttpServletRequest request,
                       HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ResultHolder error = ResultHolder.error(HttpResultCode.FORBIDDEN.getCode(),
                HttpResultCode.FORBIDDEN.getMessage() + "[" + accessDeniedException.getMessage() + "]",
                accessDeniedException.getMessage());
        LogUtils.warn("访问 URL({}): Forbidden", request.getRequestURI(), accessDeniedException);
        response.getWriter().write(JsonUtils.toJSONString(error));
    }
}
