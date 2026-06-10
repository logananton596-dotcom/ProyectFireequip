package com.fireequipmanager.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http)) // Habilita CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 2. Permisos para Equipos
                        .requestMatchers(HttpMethod.DELETE, "/api/equipos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/equipos/*/baja").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/equipos/**").hasAnyRole("ADMIN", "ENCARGADO")
                        
                        // 3. Permisos para Usuarios (Solo Admin gestiona usuarios)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        
                        // BOMBEROS
                        .requestMatchers("/api/bomberos/**")
                        .hasAnyRole("ADMIN", "ENCARGADO")

                        // ÁREAS
                        .requestMatchers("/api/areas/**")
                        .hasAnyRole("ADMIN", "ENCARGADO")

                        // ASIGNACIONES
                        .requestMatchers("/api/asignaciones/**")
                        .hasAnyRole("ADMIN", "ENCARGADO")

                        // MANTENIMIENTOS
                        .requestMatchers("/api/mantenimientos/**")
                        .hasAnyRole("ADMIN", "ENCARGADO")
                        // 4. Todo lo demás requiere estar logueado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}