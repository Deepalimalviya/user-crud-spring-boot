package com.deepali.usercrud.security;

import com.deepali.usercrud.common.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.time.Instant;
import java.util.Map;

@Configuration
public class SecurityConfig {
    @Bean UserDetailsService userDetailsService(PasswordEncoder encoder,
        @Value("${app.demo.username}") String username, @Value("${app.demo.password}") String password) {
        return new InMemoryUserDetailsManager(User.withUsername(username).password(encoder.encode(password)).roles("USER").build());
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwt, ObjectMapper mapper) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/login", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/actuator/health").permitAll().anyRequest().authenticated())
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, cause) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); res.setContentType("application/json");
                mapper.writeValue(res.getOutputStream(), new ApiError(Instant.now(), 401, "Unauthorized", "Valid Bearer token required", req.getRequestURI(), Map.of()));
            }).accessDeniedHandler((req,res,cause) -> {
                res.setStatus(403); res.setContentType("application/json");
                mapper.writeValue(res.getOutputStream(), new ApiError(Instant.now(), 403, "Forbidden", "Access denied", req.getRequestURI(), Map.of()));
            }))
            .sessionManagement(s -> s.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class).httpBasic(b -> b.disable()).formLogin(f -> f.disable()).build();
    }
}