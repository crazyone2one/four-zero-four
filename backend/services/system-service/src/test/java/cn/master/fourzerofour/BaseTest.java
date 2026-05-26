package cn.master.fourzerofour;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseTest {
    @Resource
    protected MockMvc mockMvc;
    public static final String DEFAULT_PROJECT_ID = "100001100001";
    public static final String DEFAULT_ORGANIZATION_ID = "100001";
}
