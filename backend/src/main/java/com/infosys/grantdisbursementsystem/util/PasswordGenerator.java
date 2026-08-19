package com.infosys.grantdisbursementsystem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "password123";
        String hash = encoder.encode(password);

        System.out.println("BCrypt hash:");
        System.out.println(hash);

        System.out.println("Matches:");
        System.out.println(encoder.matches(password, hash));
    }
}