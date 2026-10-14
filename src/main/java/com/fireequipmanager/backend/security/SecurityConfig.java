package com.fireequipmanager.backend.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .cors(Customizer.withDefaults()) // Habilita CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 2. Permisos para Equipos
                        .requestMatchers(HttpMethod.DELETE, "/api/equipos/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ENCARGADO")
                        .requestMatchers(HttpMethod.PUT, "/api/equipos/*/baja").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/equipos/**").hasAnyAuthority("ROLE_ADMIN")
                        
                        // 3. Permisos para Usuarios (Solo Admin gestiona usuarios)
                        .requestMatchers("/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN")
                                
                        // BOMBEROS
                        .requestMatchers("/api/bomberos/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ENCARGADO")

                        // ÁREAS
                        .requestMatchers("/api/areas/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ENCARGADO")
                        // ASIGNACIONES
                        .requestMatchers("/api/asignaciones/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ENCARGADO")

                        // MANTENIMIENTOS
                        .requestMatchers("/api/mantenimientos/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ENCARGADO")

                        .requestMatchers("/api/tipos-equipo/**")
                        .hasAnyAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/estados-equipo/**")
                        .hasAnyRole("ADMIN")

                        // 4. Todo lo demás requiere estar logueado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite explícitamente los orígenes de tu Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200"));
        // Permite los métodos HTTP estándar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite todos los encabezados (esencial para que viaje el token de Authorization)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permite el uso de credenciales, cookies o autenticación basada en cabeceras
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas de la API
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}