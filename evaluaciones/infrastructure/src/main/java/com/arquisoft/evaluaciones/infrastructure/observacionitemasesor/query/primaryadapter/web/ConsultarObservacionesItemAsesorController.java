package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.ConsultarObservacionesItemAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.dto.ObservacionItemAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper.ConsultarObservacionesItemAsesorRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper.ObservacionItemAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
public class ConsultarObservacionesItemAsesorController {

    private final ConsultarObservacionesItemAsesorInteractor consultarObservacionesItemAsesorInteractor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-asesor.observaciones-asesor:"
            + "/{evaluacionCuantitativaAsesorId}/observaciones/asesor}")
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemAsesor.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<ObservacionItemAsesorResponseDTO>> consultarObservaciones(
            @PathVariable UUID evaluacionCuantitativaAsesorId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var query = ConsultarObservacionesItemAsesorRequestMapper.toQuery(
                request, evaluacionCuantitativaAsesorId, identidad.id());
        var resultado = consultarObservacionesItemAsesorInteractor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ObservacionItemAsesorResponseMapper::toResponse)));
    }
}
