package com.payment.rtgsimps.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    /**
     * Production security configuration.
     *
     * Active when neither the "local" nor "test" profile is enabled.
     */
    @Bean
    @Profile("!local & !test")
    public SecurityFilterChain production(HttpSecurity http) throws Exception {

        http
            // Disable CSRF because this API uses stateless authentication.
            .csrf(csrf -> csrf.disable())

            // Do not create HTTP sessions.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Allow health-check endpoints without authentication.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()

                // Require authentication for every other endpoint.
                .anyRequest().authenticated()
            )

            // Validate JWT bearer tokens using Spring Security's
            // OAuth2 Resource Server support.
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    /**
     * Local and test security configuration.
     *
     * All requests are permitted, and a fixed local customer principal
     * is added to the SecurityContext for development and testing.
     */
    @Bean
    @Profile({"local", "test"})
    public SecurityFilterChain local(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authentication is bypassed in local and test environments.
            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            )

            // Set a local customer principal before the anonymous
            // authentication filter runs.
            .addFilterBefore(
                new LocalPrincipalFilter(),
                AnonymousAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Development-only filter that supplies a fixed authenticated user.
     */
    static class LocalPrincipalFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    "local-customer",
                    "N/A",
                    List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                    )
                );

            SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        }
    }
}
