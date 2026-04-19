package br.com.locci.adscreen.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String raw = "admin123";
        String hash = encoder.encode(raw);

        System.out.println("Senha original: " + raw);
        System.out.println("Hash gerado: " + hash);
    }
}
