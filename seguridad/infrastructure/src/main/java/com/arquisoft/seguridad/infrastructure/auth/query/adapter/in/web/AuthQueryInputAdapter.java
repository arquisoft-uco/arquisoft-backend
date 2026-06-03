package com.arquisoft.seguridad.infrastructure.auth.query.adapter.in.web;

import com.arquisoft.seguridad.application.auth.query.criteria.ValidateTokenCriteria;
import com.arquisoft.seguridad.application.auth.query.port.in.ValidateTokenInputPort;
import com.arquisoft.seguridad.application.auth.query.readmodel.TokenValidationReadModel;
import com.arquisoft.seguridad.infrastructure.util.message.SeguridadInfraestructureMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Seguridad - Autenticacion", description = "Consultas de autenticacion: validacion de tokens")
public class AuthQueryInputAdapter {

    private final ValidateTokenInputPort validateTokenInputPort;

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
                            schema = @Schema(implementation = TokenValidationReadModel.class))
            ),
            @ApiResponse(responseCode = "400", description = "Parametro token ausente",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TokenValidationReadModel> validateToken(@RequestParam String token) {
        log.debug(SeguridadInfraestructureMessages.AuthCommandInputAdapter.VALIDATE_DEBUG);

        ValidateTokenInputPort.ValidationResult result = validateTokenInputPort.ejecutar(
                new ValidateTokenCriteria(token)
        );

        TokenValidationReadModel response = TokenValidationReadModel.builder()
                .valid(result.valid())
                .keycloakUserId(result.keycloakUserId())
                .email(result.email())
                .message(result.message())
                .build();

        return ResponseEntity.ok(response);
    }
}
