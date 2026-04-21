package com.delissa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {

    // 🔐 ENCODER (LO DEJAMOS IGUAL)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔥 CORS CONFIG (IMPORTANTE)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173")); // frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // 🔥 SECURITY FILTER
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔥 ACTIVAMOS CORS
            .csrf(csrf -> csrf.disable()) // 🔥 DESACTIVAMOS CSRF (para API)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔥 TODO LIBRE (por ahora)
            );

        return http.build();
    }
}