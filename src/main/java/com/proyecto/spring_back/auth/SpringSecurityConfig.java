package com.proyecto.spring_back.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import java.util.Arrays;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import com.proyecto.spring_back.auth.filter.JwtAuthenticationFilter;
import com.proyecto.spring_back.auth.filter.JwtValidationFilter;

/**
 * Configuración principal de Spring Security para la aplicación de usuarios.
 * Esta clase define cómo se maneja la autenticación y autorización en la aplicación.
 */
@Configuration
public class SpringSecurityConfig {

    /**
     * Inyección de la configuración de autenticación de Spring Security.
     * Permite acceder al AuthenticationManager configurado por defecto.
     */
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    /**
     * Bean que proporciona el AuthenticationManager de Spring Security.
     * Este bean es necesario para el filtro JWT personalizado.
     * @return AuthenticationManager configurado
     * @throws Exception si hay un error en la configuración
     */
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean que proporciona el codificador de contraseñas.
     * Utiliza BCrypt para el hash seguro de contraseñas.
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración principal de la cadena de filtros de seguridad.
     * Define las reglas de autorización para diferentes endpoints y métodos HTTP.
     * 
     * @param http objeto HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain configurado
     * @throws Exception si hay un error en la configuración
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authz -> authz
                // Rutas de documentación OpenAPI/Swagger públicas
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Endpoints públicos - accesibles sin autenticación
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
                // Endpoints que requieren rol USER o ADMIN
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("USER", "ADMIN")
                // Endpoints que requieren rol ADMIN exclusivamente
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Agrega el filtro JWT personalizado para la autenticación
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                // Deshabilita CSRF ya que se usa JWT (stateless)
                .csrf(config -> config.disable())
                // Configura la gestión de sesiones como stateless (sin estado)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}