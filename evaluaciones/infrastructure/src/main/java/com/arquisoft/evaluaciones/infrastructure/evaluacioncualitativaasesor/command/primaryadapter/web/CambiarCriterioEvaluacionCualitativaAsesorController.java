package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.CambiarCriterioEvaluacionCualitativaAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.CambiarCriterioEvaluacionCualitativaAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper.CambiarCriterioEvaluacionCualitativaAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-cualitativas-asesor.base:/evaluaciones/evaluaciones-cualitativas-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_DESCRIPTION)
public class CambiarCriterioEvaluacionCualitativaAsesorController {

    private final CambiarCriterioEvaluacionCualitativaAsesorInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.evaluaciones-cualitativas-asesor.criterio:/{evaluacionCualitativaAsesorId}/criterio}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CAMBIAR_CRITERIO_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CAMBIAR_CRITERIO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CAMBIAR_CRITERIO_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CAMBIAR_CRITERIO_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CAMBIAR_CRITERIO_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarCriterio(
            @PathVariable UUID evaluacionCualitativaAsesorId,
            @RequestBody CambiarCriterioEvaluacionCualitativaAsesorRequestDTO request,
            IdentidadActual identidad) {
        interactor.ejecutar(CambiarCriterioEvaluacionCualitativaAsesorRequestMapper.toCommand(
                request, evaluacionCualitativaAsesorId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
