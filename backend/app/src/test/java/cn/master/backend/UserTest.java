package cn.master.backend;

import cn.master.system.entity.SystemUser;
import cn.master.system.mapper.SystemUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
@SpringBootTest
public class UserTest {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;

    @Test
    void contextLoads() {
        SystemUser systemUser = SystemUser.builder()
                .email("admin@malo.com")
                .password(passwordEncoder.encode("123456"))
                .name("admin")
                .enable(true)
                .createTime(LocalDateTime.now()).updateTime(LocalDateTime.now())
                .createUser("admin").updateUser("admin")
                .build();
        systemUserMapper.insert(systemUser);
    }
}
