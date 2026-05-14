package cn.master.fourzerofour;

import cn.master.util.Translator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FourZeroFourApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(Translator.get("options"));
    }
}
