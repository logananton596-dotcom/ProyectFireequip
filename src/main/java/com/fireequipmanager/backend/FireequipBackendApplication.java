package com.fireequipmanager.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.fireequipmanager.backend.model")
public class FireequipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FireequipBackendApplication.class, args);
    }
}
