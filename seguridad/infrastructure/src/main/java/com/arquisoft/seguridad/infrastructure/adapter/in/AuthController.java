package com.arquisoft.seguridad.infrastructure.adapter.in;

import com.arquisoft.seguridad.domain.port.in.JwtTokenProvider;
import com.arquisoft.seguridad.domain.port.in.KeycloakAuthService;
import com.arquisoft.seguridad.domain.model.LoginRequestDTO;
import com.arquisoft.seguridad.domain.model.LoginResponseDTO;
import com.arquisoft.seguridad.domain.model.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.application.dto.TokenValidationResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 * Expone endpoints para login, refresh token, logout y validación.
 * 
 * Endpoints:
 * - POST /api/auth/login - Autentica contra Keycloak
 * - POST /api/auth/refresh - Refresca el access token
 * - POST /api/auth/logout - Invalida el token (actualmente solo del lado del cliente)
 * - POST /api/auth/validate - Valida un token JWT
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final KeycloakAuthService keycloakAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Autentica al usuario contra Keycloak usando email y contraseña.
     * 
     * @param loginRequest contiene email y password
     * @return tokens de acceso y refresh
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getEmail());    
        LoginResponseDTO response = keycloakAuthService.authenticate(loginRequest);
        log.info("User {} logged in successfully", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Refresca el access token usando un refresh token válido.
     * 
     * @param refreshTokenRequest contiene el refresh token
     * @return nuevo access token y refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug("Token refresh attempt");
        
        LoginResponseDTO response = keycloakAuthService.refreshToken(refreshTokenRequest);
        log.debug("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Logout - Actualmente solo notifica al cliente que invalide el token.
     * En una implementación con Redis se podría mantener un blacklist de tokens.
     * 
     * @return confirmación de logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout endpoint called - Token invalidation is client-side");
        
        return ResponseEntity.ok(new Object() {
            public String message = "Sesión cerrada exitosamente. Por favor, elimina el token almacenado.";
        });
    }

    /**
     * Valida un token JWT sin requerirlo en el header.
     * Útil para validaciones desde otros servicios.
     * 
     * @param token el token a validar
     * @return información de validación
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validateToken(@RequestParam String token) {
        log.debug("Token validation attempt");
        
        try {
            if (jwtTokenProvider.validateToken(token)) {
                var userInfo = jwtTokenProvider.extractUserFromToken(token);
                return ResponseEntity.ok(TokenValidationResponseDTO.builder()
                        .valid(true)
                        .keycloakUserId(userInfo.getKeycloakUserId())
                        .email(userInfo.getEmail())
                        .message("Token válido")
                        .build());
            } else {
                return ResponseEntity.ok(TokenValidationResponseDTO.builder()
                        .valid(false)
                        .message("Token inválido o expirado")
                        .build());
            }
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return ResponseEntity.ok(TokenValidationResponseDTO.builder()
                    .valid(false)
                    .message("Error al validar token: " + e.getMessage())
                    .build());
        }
    }
}
