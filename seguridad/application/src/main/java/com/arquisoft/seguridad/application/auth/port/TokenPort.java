package com.arquisoft.seguridad.application.auth.port;

import java.util.Map;

/**
 * Puerto de salida para validacion y extraccion de informacion de tokens JWT.
 * La aplicacion define el contrato; la infraestructura implementa el parseo
 * usando la libreria JWT concreta (Spring Security OAuth2, JJWT, etc.).
 *
 * Nota: Se retorna Map<String, Object> para mantener la aplicacion libre de
 * dependencias de framework. La traduccion a DTOs ocurre en la capa de aplicacion.
 */
public interface TokenPort {

    /**
     * Extrae la informacion del usuario desde el JWT.
     *
     * @param token el token JWT
     * @return mapa con los claims del usuario (keycloakUserId, email, name, roles, issuedAt, expiresAt)
     * @throws com.arquisoft.seguridad.domain.exception.InvalidTokenException si el token es invalido
     */
    Map<String, Object> extractUserInfo(String token);

    /**
     * Valida que el token sea valido y no este expirado.
     *
     * @param token el token JWT
     * @return true si el token es valido
     */
    boolean validateToken(String token);
}
