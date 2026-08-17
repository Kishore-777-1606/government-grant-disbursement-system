package com.infosys.grantdisbursementsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("========================================");
        System.out.println("JWT FILTER");
        System.out.println("Request: " + request.getMethod()
                + " " + request.getRequestURI());
        System.out.println("Authorization header present: "
                + (authHeader != null));
        System.out.println("========================================");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {

                if (jwtUtil.isTokenValid(token)) {

                    String username =
                            jwtUtil.extractUsername(token);

                    String role =
                            jwtUtil.extractRole(token);

                    // Normalize role
                    if (role != null && role.startsWith("ROLE_")) {
                        role = role.substring(5);
                    }

                    if (role != null && !role.isBlank()) {

                        String authority = "ROLE_" + role.toUpperCase();

                        var authorities = List.of(
                                new SimpleGrantedAuthority(authority)
                        );

                        var authentication =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        authorities
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);

                        System.out.println(
                                "JWT authentication successful"
                        );
                        System.out.println(
                                "Username: " + username
                        );
                        System.out.println(
                                "Role: " + role
                        );
                        System.out.println(
                                "Authority: " + authority
                        );

                    } else {

                        System.out.println(
                                "JWT has no valid role"
                        );
                    }

                } else {

                    System.out.println(
                            "JWT token is invalid or expired"
                    );
                }

            } catch (Exception e) {

                System.out.println(
                        "JWT processing failed: "
                                + e.getMessage()
                );

                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}