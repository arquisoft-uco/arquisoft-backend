package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.application.auth.dto.LoginRequestDTO;
import com.arquisoft.seguridad.application.auth.dto.LoginResponseDTO;
import com.arquisoft.seguridad.application.auth.dto.LogoutRequestDTO;
import com.arquisoft.seguridad.application.auth.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.application.auth.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.application.auth.dto.TokenValidationReadModel;
import com.arquisoft.seguridad.application.auth.command.AuthenticateUserInputPort;
import com.arquisoft.seguridad.application.auth.command.LogoutInputPort;
import com.arquisoft.seguridad.application.auth.command.RefreshTokenInputPort;
import com.arquisoft.seguridad.application.auth.query.ValidateTokenInputPort;
import com.arquisoft.seguridad.infrastructure.util.message.SeguridadInfraestructureMessages;
import com.arquisoft.shared.util.UtilObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Seguridad - Autenticacion", description = "Autenticacion via Keycloak: login, refresh, logout y validacion de tokens JWT")
public class AuthInputAdapter {

    private final AuthenticateUserInputPort authenticateUserInputPort;
    private final RefreshTokenInputPort refreshTokenInputPort;
    private final ValidateTokenInputPort validateTokenInputPort;
    private final LogoutInputPort logoutInputPort;

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesion",
            description = "Autentica al usuario contra Keycloak usando email y contrasena. "
                    + "Retorna access token, refresh token y metadatos de la sesion."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticacion exitosa — tokens retornados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada invalidos (email o password vacios)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales incorrectas",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthenticateUserInputPort.AuthResult result = authenticateUserInputPort.authenticate(
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

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar token",
            description = "Obtiene un nuevo access token usando un refresh token valido. "
                    + "El refresh token anterior queda invalidado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refrescado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token ausente o con formato invalido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token expirado o revocado",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug(SeguridadInfraestructureMessages.AuthInputAdapter.REFRESH_DEBUG);

        RefreshTokenInputPort.RefreshResult result = refreshTokenInputPort.refresh(
                refreshTokenRequest.getRefreshToken()
        );

        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        log.debug(SeguridadInfraestructureMessages.AuthInputAdapter.REFRESH_EXITOSO_DEBUG);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesion",
            description = "Invalida el token JWT actual en la blacklist de Redis. " +
                          "El token queda rechazado hasta su expiracion natural aunque se presente con firma valida.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesion cerrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LogoutResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<LogoutResponseDTO> logout(@AuthenticationPrincipal Jwt jwt) {
        // jwt != null garantizado: /auth/logout requiere autenticacion (anyRequest().authenticated())
        String jti = jwt.getId();

        if (UtilObject.isNull(jti)) {
            // log.warn: error de cliente — token sin claim jti, situacion anormal
            log.warn(SeguridadInfraestructureMessages.AuthInputAdapter.LOGOUT_SIN_JTI);
            return ResponseEntity.ok(LogoutResponseDTO.builder().build());
        }

        Instant expiresAt = jwt.getExpiresAt();
        long remainingSeconds = 0;
        if (!UtilObject.isNull(expiresAt)) {
            remainingSeconds = Instant.now().isAfter(expiresAt)
                    ? 0
                    : Math.max(1, Duration.between(Instant.now(), expiresAt).toSeconds());
        }

        if (remainingSeconds <= 0) {
            // log.warn: error de cliente — token ya expirado al momento del logout
            log.warn(SeguridadInfraestructureMessages.AuthInputAdapter.LOGOUT_TOKEN_EXPIRADO, jti);
            return ResponseEntity.ok(LogoutResponseDTO.builder().build());
        }

        logoutInputPort.ejecutar(new LogoutRequestDTO(jti, remainingSeconds));
        // log.info: evento de negocio completado exitosamente
        log.info(SeguridadInfraestructureMessages.AuthInputAdapter.LOGOUT_EXITOSO, jti, remainingSeconds);
        return ResponseEntity.ok(LogoutResponseDTO.builder().build());
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Validar token JWT",
            description = "Valida un token JWT sin requerirlo en el header Authorization. "
                    + "Util para validaciones internas entre servicios."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultado de la validacion del token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenValidationReadModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro token ausente",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<TokenValidationReadModel> validateToken(@RequestParam String token) {
        log.debug(SeguridadInfraestructureMessages.AuthInputAdapter.VALIDATE_DEBUG);

        ValidateTokenInputPort.ValidationResult result = validateTokenInputPort.validate(token);

        TokenValidationReadModel response = TokenValidationReadModel.builder()
                .valid(result.valid())
                .keycloakUserId(result.keycloakUserId())
                .email(result.email())
                .message(result.message())
                .build();

        return ResponseEntity.ok(response);
    }
}
