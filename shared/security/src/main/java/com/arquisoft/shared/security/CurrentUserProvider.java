package com.arquisoft.shared.security;

import com.arquisoft.shared.security.domain.dto.AuthenticatedUserDTO;
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
     * Obtiene el ID del usuario actual (subject del JWT).
     */
    String getCurrentUserId();

    /**
     * Obtiene el email del usuario actual.
     */
    String getCurrentEmail();

    /**
     * Obtiene el nombre de usuario actual.
     */
    String getCurrentUsername();

    /**
     * Verifica si el usuario actual tiene un rol específico.
     */
    boolean hasRole(String role);

    /**
     * Obtiene la información completa del usuario autenticado desde el JWT.
     */
    AuthenticatedUserDTO getCurrentUser();
}
