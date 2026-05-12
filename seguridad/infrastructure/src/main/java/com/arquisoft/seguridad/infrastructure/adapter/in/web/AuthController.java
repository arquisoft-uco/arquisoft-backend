package com.arquisoft.seguridad.infrastructure.adapter.in.web;

import com.arquisoft.seguridad.application.dto.LoginRequestDTO;
import com.arquisoft.seguridad.application.dto.LoginResponseDTO;
import com.arquisoft.seguridad.application.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.application.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.application.dto.TokenValidationResponseDTO;
import com.arquisoft.seguridad.domain.port.in.AuthenticateUserUseCase;
import com.arquisoft.seguridad.domain.port.in.RefreshTokenUseCase;
import com.arquisoft.seguridad.domain.port.in.ValidateTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Seguridad - Autenticacion", description = "Autenticacion via Keycloak: login, refresh, logout y validacion de tokens JWT")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;

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

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesion",
            description = "Notifica el cierre de sesion. La invalidacion del token es responsabilidad del cliente. "
                    + "En implementaciones futuras con Redis se mantendra un blacklist de tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout registrado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LogoutResponseDTO.class)
                    )
            )
    })
    public ResponseEntity<LogoutResponseDTO> logout() {
        log.info("Endpoint de logout invocado - Invalidacion de token es del lado del cliente");
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
                            schema = @Schema(implementation = TokenValidationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parametro token ausente",
                    content = @Content(mediaType = "application/json")
            )
    })
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
