package com.infosys.grantdisbursementsystem.config;

import com.infosys.grantdisbursementsystem.security.JwtAuthFilter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // PASSWORD HASH TEST
    // =========================================================

    @Bean
    public CommandLineRunner generatePasswordHash(
            PasswordEncoder passwordEncoder) {

        return args -> {

            String hash =
                    passwordEncoder.encode("password123");

            System.out.println("========================================");
            System.out.println("BCrypt hash for password123:");
            System.out.println(hash);

            System.out.println(
                    "Password matches: "
                            + passwordEncoder.matches(
                                    "password123",
                                    hash
                            )
            );

            System.out.println("========================================");
        };
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // -------------------------------------------------
            // CORS
            // -------------------------------------------------

            .cors(Customizer.withDefaults())

            // -------------------------------------------------
            // CSRF
            // -------------------------------------------------

            .csrf(csrf -> csrf.disable())

            // -------------------------------------------------
            // STATELESS JWT AUTHENTICATION
            // -------------------------------------------------

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // -------------------------------------------------
            // AUTHORIZATION
            // -------------------------------------------------

            .authorizeHttpRequests(auth -> auth

                    // Preflight requests never carry a token — must be
                    // allowed through
                    .requestMatchers(
                            org.springframework.http.HttpMethod.OPTIONS,
                            "/**"
                    ).permitAll()

                    // Public — no token needed to hit login or the root
                    // health-check page
                    .requestMatchers(
                            "/api/auth/**",
                            "/"
                    ).permitAll()

                    // Everything else just needs a valid token; specific
                    // role restrictions are added per-endpoint via
                    // @PreAuthorize (see AuthController for an example).
                    .anyRequest().authenticated()
            )

            // -------------------------------------------------
            // JWT FILTER
            // -------------------------------------------------

            .addFilterBefore(
                    jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}