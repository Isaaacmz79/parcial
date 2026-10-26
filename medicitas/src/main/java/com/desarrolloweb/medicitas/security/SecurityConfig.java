package com.desarrolloweb.medicitas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MEDICO = "MEDICO";
    private static final String RECEPCIONISTA = "RECEPCIONISTA";
    private static final String AUDITOR = "AUDITOR";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(authenticationEntryPoint)   // 401
                    .accessDeniedHandler(accessDeniedHandler))            // 403
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers("/auth/**", "/error").permitAll()

                    // ---------- MEDICOS ----------
                    .requestMatchers(HttpMethod.GET, "/api/medicos", "/api/medicos/*")
                            .hasAnyRole(ADMIN, MEDICO, RECEPCIONISTA, AUDITOR)
                    .requestMatchers(HttpMethod.POST, "/api/medicos").hasRole(ADMIN)
                    .requestMatchers(HttpMethod.PUT, "/api/medicos/*").hasRole(ADMIN)
                    .requestMatchers(HttpMethod.DELETE, "/api/medicos/*").hasRole(ADMIN)

                    // ---------- PACIENTES ----------
                    .requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/*")
                            .hasAnyRole(ADMIN, MEDICO, RECEPCIONISTA, AUDITOR)
                    .requestMatchers(HttpMethod.POST, "/api/pacientes").hasAnyRole(ADMIN, RECEPCIONISTA)
                    .requestMatchers(HttpMethod.PUT, "/api/pacientes/*").hasAnyRole(ADMIN, RECEPCIONISTA)
                    .requestMatchers(HttpMethod.DELETE, "/api/pacientes/*").hasRole(ADMIN)

                    // ---------- CITAS ----------
                    .requestMatchers(HttpMethod.GET, "/api/citas/resumen-medicos")
                            .hasAnyRole(ADMIN, AUDITOR)
                    .requestMatchers(HttpMethod.GET, "/api/citas", "/api/citas/*")
                            .hasAnyRole(ADMIN, MEDICO, RECEPCIONISTA, AUDITOR)
                    .requestMatchers(HttpMethod.POST, "/api/citas").hasAnyRole(ADMIN, RECEPCIONISTA)
                    .requestMatchers(HttpMethod.PUT, "/api/citas/*").hasAnyRole(ADMIN, RECEPCIONISTA)
                    .requestMatchers(HttpMethod.PATCH, "/api/citas/*/estado").hasAnyRole(ADMIN, MEDICO)
                    .requestMatchers(HttpMethod.DELETE, "/api/citas/*").hasRole(ADMIN)

                    // Todo lo demas, denegado
                    .anyRequest().denyAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
