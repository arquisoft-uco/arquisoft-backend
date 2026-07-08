package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in.RegistrarEvaluacionFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web.dto.RegistrarEvaluacionFichaPerfilResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(
        name = "Evaluaciones de Ficha de Perfil",
        description = "Gestión de evaluaciones de fichas de perfil por el comité de currículum"
)
public class RegistrarEvaluacionFichaPerfilInputAdapter {

    private final RegistrarEvaluacionFichaPerfilInputPort registrarEvaluacionFichaPerfilInputPort;

    @PostMapping("/{fichaId}/evaluaciones")
    @PreAuthorize("hasAuthority('fichas:evaluacion-ficha-perfil:create')")
    @Operation(
            summary = "Registrar nueva evaluación de ficha de perfil",
            description = "Permite al representante del comité de currículum registrar una nueva evaluación sobre una ficha de perfil existente.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evaluación creada exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ficha no encontrada, representante no encontrado, "
                            + "evaluación duplicada o datos inválidos"
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos — no posee la autoridad "
                            + "fichas:evaluacion-ficha-perfil:create"
            )
    })
    public ResponseEntity<RegistrarEvaluacionFichaPerfilResponseDTO> registrarEvaluacion(
            @PathVariable UUID fichaId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID representanteComiteId = UUID.fromString(jwt.getSubject());

        var command = new RegistrarEvaluacionFichaPerfilCommand(
                fichaId,
                representanteComiteId);

        UUID evaluacionId = registrarEvaluacionFichaPerfilInputPort.ejecutar(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarEvaluacionFichaPerfilResponseDTO(evaluacionId));
    }
}
