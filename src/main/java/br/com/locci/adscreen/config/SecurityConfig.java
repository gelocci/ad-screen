package br.com.locci.adscreen.config;

import br.com.locci.adscreen.auth.filter.JwtAuthFilter;
import br.com.locci.adscreen.user.service.DatabaseUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final DatabaseUserDetailsService databaseUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(
            DatabaseUserDetailsService databaseUserDetailsService,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.databaseUserDetailsService = databaseUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .userDetailsService(databaseUserDetailsService)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/auth/login",
                    "/activate",
                    "/activate/status",
                    "/login",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/actuator/health",
                    "/actuator/info",
                    "/register"
                ).permitAll()
                .requestMatchers("/admin/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException) -> {
            String redirectUrl = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null) {
                redirectUrl += "?" + query;
            }
            response.sendRedirect("/?redirect=" + java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
