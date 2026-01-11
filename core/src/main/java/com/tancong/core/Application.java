package com.tancong.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello wo
 *
 */
@SpringBootApplication(scanBasePackages = "com.tancong")
@MapperScan("com.tancong.core.mapper")
@EnableScheduling       // 启动类开启定时任务
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

