package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.ModificarObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.ModificarObservacionItemAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper.ModificarObservacionItemAsesorRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.observaciones-item-asesor.base:/evaluaciones/observaciones-item-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_DESCRIPTION)
public class ModificarObservacionItemAsesorController {

    private final ModificarObservacionItemAsesorInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.observaciones-item-asesor.por-id:/{observacionId}}")
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemAsesor.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemAsesor.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID observacionId,
            @RequestBody ModificarObservacionItemAsesorRequestDTO request,
            IdentidadActual identidad) {
        interactor.ejecutar(
                ModificarObservacionItemAsesorRequestMapper.toCommand(request, observacionId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
