package cn.master.system.controller;

import cn.master.result.HttpResultCode;
import cn.master.result.ResultHolder;
import cn.master.security.context.SecurityUser;
import cn.master.security.dto.LoginResultDTO;
import cn.master.security.dto.RefreshDTO;
import cn.master.security.jwt.JwtTokenProvider;
import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.LoginDTO;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.entity.Project;
import cn.master.system.mapper.ProjectMapper;
import cn.master.system.service.SimpleUserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@RestController
@RequestMapping
@Tag(name = "auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final SimpleUserService simpleUserService;
    private final ProjectMapper projectMapper;

    @Value("${spring.messages.default-locale}")
    private String defaultLocale;

    @GetMapping(value = "/is-login")
    @Operation(summary = "是否登录")
    public ResultHolder isLogin(HttpServletResponse response) {
        SecurityUser securityUser = SecurityUtils.getCurrentUser();
        if (Objects.nonNull(securityUser)) {
            UserDTO userDTO = simpleUserService.getUser(securityUser.getUserId());
            if (StringUtils.isBlank(userDTO.getLanguage())) {
                userDTO.setLanguage(defaultLocale.replace("_", "-"));
            }
            simpleUserService.autoSwitch(userDTO);
            Project lastProject = projectMapper.selectOneById(userDTO.getLastProjectId());
            if (StringUtils.isBlank(userDTO.getLastProjectId()) || lastProject == null || !lastProject.getEnable()) {
                userDTO.setLastProjectId("no_such_project");
            }
            return ResultHolder.success(userDTO);
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResultHolder.error(HttpResultCode.UNAUTHORIZED.getCode(), null);
    }

    @PostMapping("/login")
    public ResultHolder login(@RequestBody @Validated LoginDTO dto) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                StringUtils.trim(dto.username()),
                                StringUtils.trim(dto.password()))
                );
        LoginResultDTO loginResultDTO = tokenProvider.generateToken(authentication.getName());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", loginResultDTO.accessToken());
        result.put("refreshToken", loginResultDTO.refreshToken());
        result.put("user", simpleUserService.getUser(SecurityUtils.getUserId()));
        return ResultHolder.success(result);
    }

    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }

    @PostMapping("/refresh-token")
    public ResultHolder refreshToken(@RequestBody RefreshDTO refreshToken) {

        LoginResultDTO result = tokenProvider.refreshAccessToken(refreshToken.refreshToken());
        if (result == null) {
            return ResultHolder.error(401, "Refresh token 无效或已过期");
        }
        return ResultHolder.success(result);
    }

    @PostMapping("/logout")
    public ResultHolder logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Claims claims = tokenProvider.parse(token);
        long expire = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        tokenProvider.addBlacklist(token, expire);
        tokenProvider.removeAccessToken(token);
        return ResultHolder.success("logout success");
    }
}
