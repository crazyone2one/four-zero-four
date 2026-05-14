package cn.master.system.controller;

import cn.master.result.ResultHolder;
import cn.master.security.dto.LoginResultDTO;
import cn.master.security.dto.RefreshDTO;
import cn.master.security.jwt.JwtTokenProvider;
import cn.master.system.dto.LoginDTO;
import cn.master.system.entity.SystemUser;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.util.MultiCacheManager;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@RestController
@RequestMapping
@Tag(name = "auth")
@RequiredArgsConstructor
public class DemoController {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MultiCacheManager multiCacheManager;
    private final SystemUserMapper systemUserMapper;


    @GetMapping("/demo")
    public String demo() {
        SystemUser systemUser = multiCacheManager.get("demo", new TypeReference<>() {
        }, () -> systemUserMapper.selectOneById("98141957388000115"));
        return systemUser.getName();
    }

    @PostMapping("/login")
    public ResultHolder login(@RequestBody @Validated LoginDTO dto) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.username(),
                                dto.password())
                );
        return ResultHolder.success(tokenProvider.generateToken(authentication.getName()));
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
