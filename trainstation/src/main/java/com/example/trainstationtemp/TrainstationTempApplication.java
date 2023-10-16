package com.example.trainstationtemp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.trainstationtemp.repository")
public class TrainstationTempApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainstationTempApplication.class, args);
    }
}
