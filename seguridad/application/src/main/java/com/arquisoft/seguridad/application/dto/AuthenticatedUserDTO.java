package com.arquisoft.seguridad.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO que contiene la información del usuario actual extraída del token JWT.
 * Se utiliza para pasar información del usuario autenticado entre contextos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedUserDTO {
    
    /**
     * ID único del usuario en Keycloak (subject del JWT)
     */
    private String keycloakUserId;
    
    /**
     * Email del usuario (única forma de contacto inicial)
     */
    private String email;
    
    /**
     * Nombre del usuario (si está disponible en Keycloak)
     */
    private String name;
    
    /**
     * Lista de roles asignados al usuario
     */
    private List<String> roles;
    
    /**
     * Marca de tiempo de emisión del token
     */
    private Long issuedAt;
    
    /**
     * Marca de tiempo de expiración del token
     */
    private Long expiresAt;
    
    /**
     * Verificar si el usuario tiene un rol específico
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
