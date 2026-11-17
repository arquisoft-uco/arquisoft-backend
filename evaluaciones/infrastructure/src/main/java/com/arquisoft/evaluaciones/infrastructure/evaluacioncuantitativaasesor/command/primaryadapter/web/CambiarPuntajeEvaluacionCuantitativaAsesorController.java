package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.CambiarPuntajeEvaluacionCuantitativaAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper.CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-asesor.base:/evaluaciones/evaluaciones-cuantitativas-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_DESCRIPTION)
public class CambiarPuntajeEvaluacionCuantitativaAsesorController {

    private final CambiarPuntajeEvaluacionCuantitativaAsesorInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-asesor.puntaje:/{evaluacionCuantitativaAsesorId}/puntaje}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CAMBIAR_PUNTAJE_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CAMBIAR_PUNTAJE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CAMBIAR_PUNTAJE_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CAMBIAR_PUNTAJE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CAMBIAR_PUNTAJE_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarPuntaje(
            @PathVariable UUID evaluacionCuantitativaAsesorId,
            @RequestBody CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO request,
            IdentidadActual identidad) {
        interactor.ejecutar(CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapper.toCommand(
                request, evaluacionCuantitativaAsesorId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
