package com.arquisoft.shared.security;

import org.springframework.security.core.Authentication;

/**
 * Interfaz para obtener información del usuario autenticado.
 * Los contextos pueden usar esto para obtener el usuario actual.
 */
public interface CurrentUserProvider {
    /**
     * Obtiene la autenticación actual del contexto de seguridad.
     */
    Authentication getCurrentAuthentication();

    /**
     * Obtiene el ID del usuario actual.
     */
    String getCurrentUserId();

    /**
     * Obtiene el nombre de usuario actual.
     */
    String getCurrentUsername();

    /**
     * Verifica si el usuario actual tiene un rol específico.
     */
    boolean hasRole(String role);
}
