package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.AutenticarUsuarioInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.CerrarSesionInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.RefrescarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.LoginRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.LoginResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.ValidateTokenResponseDTO;
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
@RequestMapping("${rutas.seguridad.auth.base:/auth}")
@RequiredArgsConstructor
@Tag(name = "Seguridad - Autenticacion", description = "Comandos de autenticacion: login, refresh y logout")
public class AutenticacionCommandController {

    private final AutenticarUsuarioInteractor autenticarUsuarioInteractor;
    private final RefrescarTokenInteractor refrescarTokenInteractor;
    private final CerrarSesionInteractor cerrarSesionInteractor;
    private final ValidarTokenInteractor validarTokenInteractor;
    private final CatalogoMensajes catalogo;

    @Deprecated(since = "OAuth 2.1 / RFC 9700 — usar Authorization Code + PKCE en la SPA")
    @PostMapping("${rutas.seguridad.auth.login:/login}")
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
    public ResponseEntity<LoginResponseDTO> iniciarSesion(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AutenticacionResult result = autenticarUsuarioInteractor.ejecutar(loginRequest.toCommand());

        var response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("${rutas.seguridad.auth.refresh:/refresh}")
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
    public ResponseEntity<LoginResponseDTO> refrescarToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug(catalogo.obtener(TokenKey.LOG_REFRESH_DEBUG));

        RefrescoTokenResult result = refrescarTokenInteractor.ejecutar(
                refreshTokenRequest.refreshToken()
        );

        var response = LoginResponseDTO.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .tokenType(result.tokenType())
                .scope(result.scope())
                .build();

        log.debug(catalogo.obtener(AutenticacionKey.LOG_REFRESCO_EXITOSO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("${rutas.seguridad.auth.logout:/logout}")
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
    public ResponseEntity<LogoutResponseDTO> cerrarSesion(@AuthenticationPrincipal Jwt jwt) {
        Instant expiracion = jwt.getExpiresAt();
        long tiempoVida = (expiracion != null && Instant.now().isBefore(expiracion))
                ? Math.max(1L, Duration.between(Instant.now(), expiracion).toSeconds())
                : 0L;
        cerrarSesionInteractor.ejecutar(new TokenSesionCommand(jwt.getId(), tiempoVida));
        return ResponseEntity.ok(LogoutResponseDTO.builder().build());
    }

    @PostMapping("${rutas.seguridad.auth.validate:/validate}")
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
    public ResponseEntity<ValidateTokenResponseDTO> validarToken(@RequestParam String token) {
        log.debug(catalogo.obtener(AutenticacionKey.LOG_VALIDAR_DEBUG));

        ValidacionTokenResult result = validarTokenInteractor.ejecutar(
                TokenDomain.de(token)
        );

        var response = ValidateTokenResponseDTO.builder()
                .valido(result.valido())
                .identidadId(result.identidadId())
                .correo(result.correo())
                .mensaje(result.mensaje())
                .build();

        return ResponseEntity.ok(response);
    }
}
