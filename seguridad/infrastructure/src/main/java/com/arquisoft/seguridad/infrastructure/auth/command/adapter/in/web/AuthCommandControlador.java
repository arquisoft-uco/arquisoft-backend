package com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.SeguridadKeys;
import com.arquisoft.seguridad.application.auth.command.interactor.AuthenticateUserInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.LogoutInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.RefreshTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.ValidateTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenDomain;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.ValidateTokenResponseDTO;
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
@Tag(name = "Seguridad - Autenticacion", description = "Comandos de autenticacion: login, refresh y logout")
public class AuthCommandControlador {

    private final AuthenticateUserInteractor authenticateUserInteractor;
    private final RefreshTokenInteractor refreshTokenInteractor;
    private final LogoutInteractor logoutInteractor;
    private final ValidateTokenInteractor validateTokenInteractor;
    private final MessageCatalog catalog;

    @Deprecated(since = "OAuth 2.1 / RFC 9700 — usar Authorization Code + PKCE en la SPA")
    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesion (ROPC — desaconsejado para navegadores)",
            description = "Autentica al usuario contra Keycloak usando email y contrasena "
                    + "(grant_type=password / ROPC). DESACONSEJADO por OAuth 2.1 y RFC 9700: "
                    + "el flujo recomendado para la SPA es Authorization Code + PKCE contra "
                    + "Keycloak. Este endpoint se mantiene solo para clientes internos de confianza. "
                    + "Retorna access token, refresh token y metadatos de la sesion.",
            deprecated = true
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticacion exitosa — tokens retornados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AutenticacionResult result = authenticateUserInteractor.ejecutar(loginRequest.toCommand());

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
            description = "Obtiene un nuevo access token usando un refresh token valido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token ausente o invalido",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Refresh token expirado o revocado",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug(catalog.obtener(SeguridadKeys.Token.LOG_REFRESH_DEBUG));

        RefrescoTokenResult result = refreshTokenInteractor.ejecutar(
                refreshTokenRequest.refreshToken()
        );

        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        log.debug(catalog.obtener(SeguridadKeys.Autenticacion.LOG_REFRESH_EXITOSO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesion",
            description = "Invalida el token JWT actual en la blacklist de Redis.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesion cerrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LogoutResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Token de sesion invalido o ya expirado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<LogoutResponseDTO> logout(@AuthenticationPrincipal Jwt jwt) {
        Instant expiracion = jwt.getExpiresAt();
        long tiempoVida = (expiracion != null && Instant.now().isBefore(expiracion))
                ? Math.max(1L, Duration.between(Instant.now(), expiracion).toSeconds())
                : 0L;
        logoutInteractor.ejecutar(new TokenSesionCommand(jwt.getId(), tiempoVida));
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
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidateTokenResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parametro token ausente",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ValidateTokenResponseDTO> validateToken(@RequestParam String token) {
        log.debug(catalog.obtener(SeguridadKeys.Autenticacion.LOG_VALIDATE_DEBUG));

        ValidacionTokenResult result = validateTokenInteractor.ejecutar(
                TokenDomain.de(token)
        );

        ValidateTokenResponseDTO response = ValidateTokenResponseDTO.builder()
                .valido(result.valido())
                .identidadId(result.identidadId())
                .correo(result.correo())
                .mensaje(result.mensaje())
                .build();

        return ResponseEntity.ok(response);
    }
}
