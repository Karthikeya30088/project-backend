package com.fsad.assignment.config;

import com.fsad.assignment.service.PortalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(PortalService portalService) {
        return args -> portalService.seedIfNeeded();
    }
}
