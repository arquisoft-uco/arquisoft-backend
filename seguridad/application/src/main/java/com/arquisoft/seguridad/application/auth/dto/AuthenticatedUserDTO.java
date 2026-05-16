package com.arquisoft.seguridad.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que contiene la informacion del usuario actual extraida del token JWT.
 * Se utiliza para pasar informacion del usuario autenticado entre capas y contextos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedUserDTO {

    /**
     * ID unico del usuario en Keycloak (subject del JWT)
     */
    private String keycloakUserId;

    /**
     * Email del usuario
     */
    private String email;

    /**
     * Nombre del usuario (si esta disponible en Keycloak)
     */
    private String name;

    /**
     * Lista de roles asignados al usuario
     */
    private List<String> roles;

    /**
     * Marca de tiempo de emision del token
     */
    private Long issuedAt;

    /**
     * Marca de tiempo de expiracion del token
     */
    private Long expiresAt;

    /**
     * Verificar si el usuario tiene un rol especifico
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
