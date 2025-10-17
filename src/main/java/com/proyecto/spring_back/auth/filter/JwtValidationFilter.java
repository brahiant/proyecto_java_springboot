package com.proyecto.spring_back.auth.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import static com.proyecto.spring_back.auth.TokenJwtConfig.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Filtro de validación JWT que se ejecuta en cada petición HTTP.
 * 
 * Este filtro se encarga de:
 * 1. Extraer el token JWT del header de autorización
 * 2. Validar la firma del token
 * 3. Extraer la información del usuario y sus roles
 * 4. Establecer la autenticación en el contexto de seguridad de Spring
 * 
 * Extiende de BasicAuthenticationFilter para integrarse con el sistema de seguridad de Spring
 */
public class JwtValidationFilter extends BasicAuthenticationFilter {

    /**
     * Constructor que recibe el AuthenticationManager para gestionar la autenticación
     * @param authenticationManager Manager de autenticación de Spring Security
     */
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * Método principal del filtro que se ejecuta en cada petición HTTP.
     * 
     * Flujo de trabajo:
     * 1. Verifica si existe el header de autorización
     * 2. Extrae y valida el token JWT
     * 3. Decodifica la información del usuario y roles
     * 4. Establece la autenticación en el contexto de seguridad
     * 5. Continúa con la cadena de filtros
     * 
     * @param request Petición HTTP entrante
     * @param response Respuesta HTTP saliente
     * @param chain Cadena de filtros a ejecutar
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Obtiene el header de autorización de la petición
        String header = request.getHeader(HEADER_AUTHORIZATION);

        // Si no hay header de autorización o no comienza con el prefijo correcto,
        // continúa con la cadena de filtros sin autenticación
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        // Extrae el token JWT removiendo el prefijo (ej: "Bearer ")
        String token = header.replace(PREFIX_TOKEN, "");
        
        try {
            // Verifica y parsea el token JWT usando la clave secreta
            // Claims contiene toda la información del token (username, roles, expiración, etc.)
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            
            // Extrae el nombre de usuario del token (subject)
            String username = claims.getSubject();
            // String username2 = (String) claims.get("username"); // Forma alternativa de obtener username
            
            // Extrae los roles/autoridades del token
            Object authoritiesClaims = claims.get("authorities");

            // Convierte los roles del token en objetos GrantedAuthority de Spring Security
            // Usa ObjectMapper con un mixin personalizado para mapear correctamente los roles
            Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            // Crea un token de autenticación con el usuario y sus roles
            // El password se establece como null ya que no es necesario en este punto
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, 
                    roles);
            
            // Establece la autenticación en el contexto de seguridad de Spring
            // Esto permite que otros componentes accedan a la información del usuario autenticado
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            
            // Continúa con la cadena de filtros
            chain.doFilter(request, response);

        } catch (JwtException e) {
            // Si hay un error al validar el token JWT, devuelve una respuesta de error
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token es invalido!");

            // Escribe la respuesta de error en formato JSON
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401); // Código de estado: Unauthorized
            response.setContentType(CONTENT_TYPE); // Establece el tipo de contenido como JSON
        }

    }
    
    

}
