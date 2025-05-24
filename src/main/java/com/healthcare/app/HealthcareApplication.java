package com.healthcare.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Healthcare Application.
 */
@SpringBootApplication(scanBasePackages = {"com.healthcare.app", "com.health.util"})
public class HealthcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareApplication.class, args);
    }

}
