package com.proyecto.spring_back.auth.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.proyecto.spring_back.models.LoginRequest;
import static com.proyecto.spring_back.auth.TokenJwtConfig.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.security.core.AuthenticationException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;

/**
 * Filtro de autenticación JWT que extiende UsernamePasswordAuthenticationFilter.
 * Este filtro se encarga de procesar las solicitudes de autenticación y generar tokens JWT
 * cuando la autenticación es exitosa.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    /** Gestor de autenticación que valida las credenciales del usuario */
    private AuthenticationManager authenticationManager;

    /**
     * Constructor que recibe el AuthenticationManager para validar credenciales
     * @param authenticationManager Gestor de autenticación de Spring Security
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Método que se ejecuta cuando se intenta autenticar un usuario.
     * Lee las credenciales del cuerpo de la solicitud HTTP y crea un token de autenticación.
     * 
     * @param request Solicitud HTTP entrante
     * @param response Respuesta HTTP de salida
     * @return Objeto Authentication con el resultado de la autenticación
     * @throws AuthenticationException Si la autenticación falla
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = null;
        String password = null;

        try {
            // Lee el cuerpo de la solicitud HTTP y lo convierte a un objeto LoginRequest
            // para extraer el nombre de usuario y contraseña
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            username = loginRequest.getUsername();
            password = loginRequest.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Crea un token de autenticación con las credenciales extraídas
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        // Delega la autenticación al AuthenticationManager
        return this.authenticationManager.authenticate(authenticationToken);
    }

    /**
     * Método que se ejecuta cuando la autenticación es exitosa.
     * Genera un token JWT con la información del usuario autenticado y lo incluye
     * en la respuesta HTTP.
     * 
     * @param request Solicitud HTTP original
     * @param response Respuesta HTTP donde se incluirá el token JWT
     * @param chain Cadena de filtros
     * @param authResult Resultado exitoso de la autenticación
     * @throws IOException Si hay error de entrada/salida
     * @throws ServletException Si hay error del servlet
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // Obtiene el usuario autenticado del resultado de la autenticación
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult
                .getPrincipal();
        String username = user.getUsername();
        // Obtiene los roles/autoridades del usuario autenticado
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        boolean isAdmin = roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        // Crea los claims (reclamaciones) del token JWT
        Claims claims = Jwts
                .claims()
                .add("authorities", new ObjectMapper().writeValueAsString(roles)) // Convierte roles a JSON
                .add("username", username) // Agrega el nombre de usuario
                .add("isAdmin", isAdmin) // Agrega el booleano isAdmin
                .build();

        // Construye el token JWT con toda la información necesaria
        String jwt = Jwts.builder()
                .subject(username) // Sujeto del token (nombre de usuario)
                .claims(claims) // Claims personalizados (roles y username)
                .signWith(SECRET_KEY) // Firma el token con la clave secreta
                .issuedAt(new Date()) // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // Expira en 1 hora (3600000 ms)
                .compact(); // Genera el token compacto

        // Agrega el token JWT al header de autorización de la respuesta
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);

        // Crea el cuerpo de la respuesta con información del login exitoso
        Map<String, String> body = new HashMap<>();
        body.put("token", jwt); // Token JWT generado
        body.put("username", username); // Nombre de usuario
        body.put("message", String.format("Hola %s has iniciado sesion con exito", username)); // Mensaje de bienvenida

        // Escribe la respuesta JSON en el cuerpo de la respuesta HTTP
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE); // Establece el tipo de contenido como JSON
        response.setStatus(200); // Establece el código de estado HTTP como 200 (OK)
    }

    /**
     * Método que se ejecuta cuando la autenticación falla.
     * Por defecto, Spring Security maneja la respuesta de error.
     * 
     * @param request Solicitud HTTP original
     * @param response Respuesta HTTP donde se incluirá el error
     * @param failed Excepción que causó el fallo en la autenticación
     * @throws IOException Si hay error de entrada/salida
     * @throws ServletException Si hay error del servlet
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        // La implementación por defecto de Spring Security maneja la respuesta de error
        // Se puede personalizar aquí si se desea un comportamiento específico
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", failed.getMessage());
        errorDetails.put("path", request.getServletPath());

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
    }

}

