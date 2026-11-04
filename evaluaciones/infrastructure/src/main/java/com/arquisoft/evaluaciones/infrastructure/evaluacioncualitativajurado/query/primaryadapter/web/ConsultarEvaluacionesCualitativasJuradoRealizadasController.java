package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.dto.EvaluacionCualitativaJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.mapper.ConsultarEvaluacionesCualitativasJuradoRealizadasRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.mapper.EvaluacionCualitativaJuradoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-jurado.base:/evaluaciones/evaluaciones-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.TAG_DESCRIPTION)
public class ConsultarEvaluacionesCualitativasJuradoRealizadasController {

    private final ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor interactor;

    @GetMapping("${rutas.evaluaciones.evaluaciones-jurado.cualitativas-jurado:/{evaluacionJuradoId}/cualitativas/jurado}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaJurado.CONSULTAR_REALIZADAS_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.CONSULTAR_REALIZADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.CONSULTAR_REALIZADAS_RESP_200,
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = EvaluacionCualitativaJuradoResponseDTO.class)))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.CONSULTAR_REALIZADAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.CONSULTAR_REALIZADAS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EvaluacionCualitativaJuradoResponseDTO>> consultarEvaluacionesCualitativasJuradoRealizadas(
            @PathVariable UUID evaluacionJuradoId,
            IdentidadActual identidad) {

        var jurado = identidad.id();
        var query = ConsultarEvaluacionesCualitativasJuradoRealizadasRequestMapper.toQuery(evaluacionJuradoId, jurado);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(EvaluacionCualitativaJuradoResponseMapper::toResponse)
                .toList());
    }
}
