package cn.master;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@MapperScan("cn.master.system.mapper")
@SpringBootApplication(scanBasePackages = "cn.master")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
