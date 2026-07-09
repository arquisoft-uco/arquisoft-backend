package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in.AgregarEstadoEvaluacionFichaInputPort;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto.AgregarEstadoEvaluacionFichaRequestDTO;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto.AgregarEstadoEvaluacionFichaResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fichas-perfil/estado-evaluacion-ficha")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Estado Evaluación Ficha", description = "Gestión de trazabilidad de estados de evaluación de fichas de perfil")
public class AgregarEstadoEvaluacionFichaInputAdapter {

    private final AgregarEstadoEvaluacionFichaInputPort agregarEstadoEvaluacionFichaInputPort;

    @PostMapping
    @PreAuthorize("hasAuthority('fichas:estado-evaluacion-ficha:create')")
    @Operation(
            summary = "Agregar estado evaluación ficha",
            description = "Registra un nuevo estado en la trazabilidad de evaluación de una ficha de perfil",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Estado agregado exitosamente",
                    content = @Content(schema = @Schema(implementation = AgregarEstadoEvaluacionFichaResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o evaluación no encontrada o estado duplicado"),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - requiere rol representante-comite"),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transición de estado inválida (estado terminal o primer estado debe ser EN_EVALUACION)")
    })
    public ResponseEntity<AgregarEstadoEvaluacionFichaResponseDTO> agregar(
            @Valid @RequestBody AgregarEstadoEvaluacionFichaRequestDTO request) {

        var id = agregarEstadoEvaluacionFichaInputPort.ejecutar(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarEstadoEvaluacionFichaResponseDTO(id));
    }
}
