package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.SeguridadApiMessages;
import com.arquisoft.shared.message.key.seguridad.AutenticacionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.Mensajes;
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
@Tag(name = SeguridadApiMessages.Autenticacion.TAG_NAME,
        description = SeguridadApiMessages.Autenticacion.TAG_DESCRIPTION)
public class AutenticacionCommandController {

    private final AutenticarUsuarioInteractor autenticarUsuarioInteractor;
    private final RefrescarTokenInteractor refrescarTokenInteractor;
    private final CerrarSesionInteractor cerrarSesionInteractor;
    private final ValidarTokenInteractor validarTokenInteractor;

    @Deprecated(since = "OAuth 2.1 / RFC 9700 — usar Authorization Code + PKCE en la SPA")
    @PostMapping("${rutas.seguridad.auth.login:/login}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.INICIAR_SESION_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_DESCRIPTION,
            deprecated = true
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_200,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_400,
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_401,
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
            summary = SeguridadApiMessages.Autenticacion.REFRESCAR_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.REFRESCAR_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_200,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_400,
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_401,
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<LoginResponseDTO> refrescarToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        log.debug(Mensajes.obtener(TokenKey.LOG_REFRESH_DEBUG));

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

        log.debug(Mensajes.obtener(AutenticacionKey.LOG_REFRESCO_EXITOSO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("${rutas.seguridad.auth.logout:/logout}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.CERRAR_SESION_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_DESCRIPTION,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_200,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LogoutResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_400),
            @ApiResponse(responseCode = "401", description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_401)
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
            summary = SeguridadApiMessages.Autenticacion.VALIDAR_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.VALIDAR_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = SeguridadApiMessages.Autenticacion.VALIDAR_RESP_200,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidateTokenResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = SeguridadApiMessages.Autenticacion.VALIDAR_RESP_400,
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ValidateTokenResponseDTO> validarToken(@RequestParam String token) {
        log.debug(Mensajes.obtener(AutenticacionKey.LOG_VALIDAR_DEBUG));

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
