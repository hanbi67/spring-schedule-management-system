package com.example.schedulemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ScheduleManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleManagementSystemApplication.class, args);
    }

}
