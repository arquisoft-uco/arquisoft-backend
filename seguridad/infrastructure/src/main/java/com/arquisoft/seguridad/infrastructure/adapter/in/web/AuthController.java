package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.application.dto.LoginRequestDTO;
import com.arquisoft.seguridad.application.dto.LoginResponseDTO;
import com.arquisoft.seguridad.application.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.application.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.application.dto.TokenValidationResponseDTO;
import com.arquisoft.seguridad.domain.port.in.AuthenticateUserUseCase;
import com.arquisoft.seguridad.domain.port.in.RefreshTokenUseCase;
import com.arquisoft.seguridad.domain.port.in.ValidateTokenUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticacion.
 * Expone endpoints para login, refresh token, logout y validacion.
 *
 * Endpoints:
 * - POST /api/auth/login - Autentica contra Keycloak
 * - POST /api/auth/refresh - Refresca el access token
 * - POST /api/auth/logout - Invalida el token (actualmente solo del lado del cliente)
 * - POST /api/auth/validate - Valida un token JWT
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;

    /**
     * Autentica al usuario contra Keycloak usando email y contrasena.
     *
     * @param loginRequest contiene email y password
     * @return tokens de acceso y refresh
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getEmail());

        AuthenticateUserUseCase.AuthResult result = authenticateUserUseCase.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        log.info("Usuario {} autenticado exitosamente", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Refresca el access token usando un refresh token valido.
     *
     * @param refreshTokenRequest contiene el refresh token
     * @return nuevo access token y refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug("Intento de refresco de token");

        RefreshTokenUseCase.RefreshResult result = refreshTokenUseCase.refresh(
                refreshTokenRequest.getRefreshToken()
        );

        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        log.debug("Token refrescado exitosamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Logout - Actualmente solo notifica al cliente que invalide el token.
     * En una implementacion con Redis se podria mantener un blacklist de tokens.
     *
     * @return confirmacion de logout
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout() {
        log.info("Endpoint de logout invocado - Invalidacion de token es del lado del cliente");

        return ResponseEntity.ok(LogoutResponseDTO.builder().build());
    }

    /**
     * Valida un token JWT sin requerirlo en el header.
     * Util para validaciones desde otros servicios.
     *
     * @param token el token a validar
     * @return informacion de validacion
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validateToken(@RequestParam String token) {
        log.debug("Intento de validacion de token");

        ValidateTokenUseCase.ValidationResult result = validateTokenUseCase.validate(token);

        TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                .valid(result.valid())
                .keycloakUserId(result.keycloakUserId())
                .email(result.email())
                .message(result.message())
                .build();

        return ResponseEntity.ok(response);
    }
}
