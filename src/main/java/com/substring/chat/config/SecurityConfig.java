package com.substring.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configure CORS using the bean defined below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF, common for stateless APIs
                .csrf(csrf -> csrf.disable())

                // 3. Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow access to your room API and WebSocket endpoint without authentication
                        .requestMatchers("/api/v1/rooms/**", "/chat/**").permitAll()
                        // Any other request needs to be authenticated (good practice)
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 4. IMPORTANT: Specify your frontend's origin
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",             // For local development
                "https://yoom-psi-two.vercel.app",   // Your main production URL
                "https://yoom-*.vercel.app",          // Pattern for all Vercel preview deployments
                "https://simple-meet-*.vercel.app",
                "https://simplemeet-*.vercel.app"
        ));


        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, etc.) if needed
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}