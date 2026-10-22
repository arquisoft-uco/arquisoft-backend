package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.dto.EvaluacionCualitativaAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper.ConsultarEvaluacionesCualitativasAsesorRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper.EvaluacionCualitativaAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-asesor.base:/evaluaciones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_DESCRIPTION)
public class ConsultarEvaluacionesCualitativasAsesorController {

    private final ConsultarEvaluacionesCualitativasAsesorInteractor interactor;

    @GetMapping("${rutas.evaluaciones.evaluaciones-asesor.cualitativas:/{evaluacionAsesorId}/cualitativas}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            schema = @Schema(implementation = EvaluacionCualitativaAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EvaluacionCualitativaAsesorResponseDTO>> consultarEvaluacionesCualitativasAsesor(
            @PathVariable UUID evaluacionAsesorId,
            @AuthenticationPrincipal Jwt jwt) {

        var asesor = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var query = ConsultarEvaluacionesCualitativasAsesorRequestMapper.toQuery(evaluacionAsesorId, asesor);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(EvaluacionCualitativaAsesorResponseMapper::toResponse)
                .toList());
    }
}
