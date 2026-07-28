package com.zimmomo.momo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MomoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MomoApplication.class, args);
    }
}
