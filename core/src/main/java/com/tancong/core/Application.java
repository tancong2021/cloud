package com.tancong.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = "com.tancong")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

