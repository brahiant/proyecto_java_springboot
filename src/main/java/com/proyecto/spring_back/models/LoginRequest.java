package com.proyecto.spring_back.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para las solicitudes de login.
 * Solo contiene username y password, sin validaciones adicionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}

