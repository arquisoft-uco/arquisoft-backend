package com.arquisoft.shared.security.application.services;

import com.arquisoft.shared.security.domain.dto.LoginRequestDTO;
import com.arquisoft.shared.security.domain.dto.LoginResponseDTO;
import com.arquisoft.shared.security.domain.dto.RefreshTokenRequestDTO;

/**
 * Servicio para manejar autenticación con Keycloak.
 * Delega al servidor de Keycloak para validar credenciales.
 */
public interface KeycloakAuthService {
    
    /**
     * Autentica al usuario contra Keycloak usando email y contraseña.
     * 
     * @param loginRequest datos de login (email y password)
     * @return tokens de acceso y refresh
     * @throws com.arquisoft.shared.security.domain.exceptions.InvalidCredentialsException 
     *         si las credenciales son inválidas
     * @throws com.arquisoft.shared.security.domain.exceptions.AuthenticationException 
     *         si hay error en la comunicación con Keycloak
     */
    LoginResponseDTO authenticate(LoginRequestDTO loginRequest);
    
    /**
     * Refresca el token de acceso usando el refresh token.
     * 
     * @param refreshTokenRequest contiene el refresh token
     * @return nuevo access token y refresh token
     * @throws com.arquisoft.shared.security.domain.exceptions.InvalidTokenException 
     *         si el refresh token es inválido o expirado
     */
    LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest);
    
    /**
     * Valida que el refresh token sea válido.
     * 
     * @param refreshToken el refresh token
     * @return true si es válido
     */
    boolean validateRefreshToken(String refreshToken);
}
