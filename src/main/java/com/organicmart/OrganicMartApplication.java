package com.organicmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OrganicMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganicMartApplication.class, args);
    }
}
