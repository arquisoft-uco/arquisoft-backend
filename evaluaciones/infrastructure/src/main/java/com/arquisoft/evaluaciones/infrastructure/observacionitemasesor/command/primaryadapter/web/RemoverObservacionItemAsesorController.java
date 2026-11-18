package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RemoverObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper.RemoverObservacionItemAsesorRequestMapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.observaciones-item-asesor.base:/evaluaciones/observaciones-item-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_DESCRIPTION)
public class RemoverObservacionItemAsesorController {

    private final RemoverObservacionItemAsesorInteractor interactor;

    @DeleteMapping("${rutas.evaluaciones.observaciones-item-asesor.por-id:/{observacionId}}")
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemAsesor.REMOVER_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemAsesor.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REMOVER_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REMOVER_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REMOVER_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> remover(@PathVariable UUID observacionId, IdentidadActual identidad) {
        interactor.ejecutar(RemoverObservacionItemAsesorRequestMapper.toCommand(observacionId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
