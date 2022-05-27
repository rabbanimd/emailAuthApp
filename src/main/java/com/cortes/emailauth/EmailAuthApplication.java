package com.cortes.emailauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmailAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailAuthApplication.class, args);
    }

}
