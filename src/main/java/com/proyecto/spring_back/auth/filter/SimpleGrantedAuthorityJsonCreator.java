package com.proyecto.spring_back.auth.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase auxiliar que actúa como "Mixin" de Jackson para la deserialización JSON
 * de objetos SimpleGrantedAuthority en tokens JWT.
 * 
 * Esta clase resuelve un problema técnico específico:
 * 
 * PROBLEMA:
 * - Cuando Spring Security serializa roles a JSON para incluirlos en tokens JWT,
 *   los objetos SimpleGrantedAuthority se convierten a: {"authority": "ROLE_ADMIN"}
 * - Al deserializar el token JWT, Jackson necesita saber cómo reconstruir estos objetos
 * - Sin esta configuración, Jackson no sabría qué constructor usar ni cómo mapear el JSON
 * 
 * SOLUCIÓN:
 * - Esta clase le dice a Jackson que use el constructor que recibe un String
 * - El parámetro "authority" del JSON se mapea al parámetro del constructor
 * - Permite reconstruir correctamente los objetos SimpleGrantedAuthority
 * 
 * USO:
 * - Se configura en JwtValidationFilter como mixin:
 *   .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
 * - No se instancia directamente, solo se usa como configuración de Jackson
 * 
 * @author Sistema de Autenticación JWT
 * @see JwtValidationFilter
 * @see SimpleGrantedAuthority
 */
public abstract class SimpleGrantedAuthorityJsonCreator {

    /**
     * Constructor anotado con @JsonCreator que Jackson usará para deserialización.
     * 
     * @param role El nombre del rol/autoridad que viene del JSON (ej: "ROLE_ADMIN")
     *             Se mapea desde el campo "authority" del JSON usando @JsonProperty
     */
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role){
        // Constructor vacío - solo se usa para mapeo de Jackson
        // La lógica real de construcción la maneja SimpleGrantedAuthority
    }
}

