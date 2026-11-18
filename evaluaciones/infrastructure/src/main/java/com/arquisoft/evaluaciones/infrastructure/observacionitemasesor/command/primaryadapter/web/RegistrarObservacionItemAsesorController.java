package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RegistrarObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.RegistrarObservacionItemAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.RegistrarObservacionItemAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper.RegistrarObservacionItemAsesorRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-asesor.base:/evaluaciones/evaluaciones-cuantitativas-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemAsesor.TAG_DESCRIPTION)
public class RegistrarObservacionItemAsesorController {

    private final RegistrarObservacionItemAsesorInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-asesor.observaciones:/{evaluacionCuantitativaAsesorId}/observaciones}")
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemAsesor.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = RegistrarObservacionItemAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarObservacionItemAsesorResponseDTO> registrar(
            @PathVariable UUID evaluacionCuantitativaAsesorId,
            @RequestBody RegistrarObservacionItemAsesorRequestDTO request,
            IdentidadActual identidad) {
        var observacionId = interactor.ejecutar(RegistrarObservacionItemAsesorRequestMapper.toCommand(
                request, evaluacionCuantitativaAsesorId, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarObservacionItemAsesorResponseDTO(observacionId));
    }
}
