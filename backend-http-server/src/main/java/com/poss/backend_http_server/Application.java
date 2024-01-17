package com.poss.backend_http_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.poss")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
