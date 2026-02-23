package com.arquisoft.seguridad.domain.port.in;

import com.arquisoft.seguridad.domain.model.AuthenticatedUserDTO;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Servicio para parsear y validar tokens JWT.
 * Extrae información del usuario desde el token.
 */
public interface JwtTokenProvider {
    
    /**
     * Extrae la información del usuario desde el JWT.
     * 
     * @param token el token JWT
     * @return información del usuario autenticado
     * @throws InvalidTokenException si el token es inválido
     */
    AuthenticatedUserDTO extractUserFromToken(String token);
    
    /**
     * Valida que el token sea válido y no esté expirado.
     * 
     * @param token el token JWT
     * @return true si el token es válido
     */
    boolean validateToken(String token);
    
    /**
     * Obtiene el objeto JWT completo.
     * 
     * @param token el token JWT
     * @return objeto Jwt
     * @throws InvalidTokenException si el token es inválido
     */
    Jwt getJwt(String token);
}
