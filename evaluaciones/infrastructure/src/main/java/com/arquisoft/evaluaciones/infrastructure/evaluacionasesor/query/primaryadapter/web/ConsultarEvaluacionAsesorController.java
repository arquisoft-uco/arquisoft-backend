package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper.ConsultarEvaluacionAsesorRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper.EvaluacionAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluacion.base:/evaluaciones}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionAsesor.TAG_DESCRIPTION)
public class ConsultarEvaluacionAsesorController {

    private final ConsultarEvaluacionAsesorInteractor consultarEvaluacionAsesorInteractor;

    @GetMapping("${rutas.evaluaciones.evaluacion.evaluacion-asesor:/{evaluacionId}/evaluacion-asesor}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EvaluacionAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<EvaluacionAsesorResponseDTO> consultarEvaluacionAsesor(
            @PathVariable UUID evaluacionId) {

        var evaluacionAsesor = consultarEvaluacionAsesorInteractor.ejecutar(
                ConsultarEvaluacionAsesorRequestMapper.toQuery(evaluacionId));

        return ResponseEntity.ok(EvaluacionAsesorResponseMapper.toResponse(evaluacionAsesor));
    }
}
