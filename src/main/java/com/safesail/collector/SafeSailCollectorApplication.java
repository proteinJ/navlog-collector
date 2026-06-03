package com.safesail.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SafeSailCollectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafeSailCollectorApplication.class, args);
    }
}
