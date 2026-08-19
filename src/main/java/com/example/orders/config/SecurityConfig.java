package com.example.orders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/products/**")
                    .hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/products/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/products/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**")
                    .hasRole("ADMIN")
                .requestMatchers(
                    "/customers/**",
                    "/orders/**",
                    "/order-items/**"
                ).authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {

        UserDetails user = User
            .withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();

        UserDetails admin = User
            .withUsername("admin")
            .password("{noop}password")
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}