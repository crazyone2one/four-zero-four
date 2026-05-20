package cn.master.system.controller;

import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.request.PersonalLocaleRequest;
import cn.master.system.service.SimpleUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 11's papa
 * @since : 2026/5/20, 星期三
 **/
@RestController
@Tag(name = "个人中心")
@RequestMapping("/personal")
@RequiredArgsConstructor
public class PersonalCenterController {
    private final SimpleUserService simpleUserService;

    @PostMapping("/update-locale")
    @Operation(summary = "个人中心-修改信息")
    public void updateLocale(@Validated @RequestBody PersonalLocaleRequest request) {
        simpleUserService.updateLanguage(request, SecurityUtils.getUserId());
    }
}
