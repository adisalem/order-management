package com.example.orders.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.orders.entity.User;
import com.example.orders.repository.UserRepository;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User(
                    "admin",
                    passwordEncoder.encode("password"),
                    "ADMIN"
                );

                userRepository.save(admin);
            }

            if (userRepository.findByUsername("user").isEmpty()) {

                User user = new User(
                    "user",
                    passwordEncoder.encode("password"),
                    "USER"
                );

                userRepository.save(user);
            }
        };
    }
}