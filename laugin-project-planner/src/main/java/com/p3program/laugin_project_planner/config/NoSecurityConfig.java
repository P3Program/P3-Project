package com.p3program.laugin_project_planner.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Config for disabling login for dev purposes. Profile is defined for application.properties
 * to know what files to launch, and SecurityFilterChain @Bean has been named to identify it
 * separately from the production config class.
 */

@Profile("dev")
@Configuration
public class NoSecurityConfig {

    @Bean("devSecurityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
