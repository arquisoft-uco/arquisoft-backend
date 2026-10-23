package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesAsignadasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto.EvaluacionAsignadaJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.ConsultarEvaluacionesAsignadasJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.EvaluacionAsignadaJuradoResponseMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-jurado.base:/evaluaciones/evaluaciones-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionJurado.TAG_DESCRIPTION)
public class ConsultarEvaluacionesAsignadasJuradoController {

    private final ConsultarEvaluacionesAsignadasJuradoInteractor interactor;

    @GetMapping("${rutas.evaluaciones.evaluaciones-jurado.jurado:/jurado}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_RESP_200,
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = EvaluacionAsignadaJuradoResponseDTO.class)))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EvaluacionAsignadaJuradoResponseDTO>> consultarEvaluacionesAsignadasJurado(
            IdentidadActual identidad) {

        var jurado = identidad.id();
        var query = ConsultarEvaluacionesAsignadasJuradoRequestMapper.toQuery(jurado);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(EvaluacionAsignadaJuradoResponseMapper::toResponse)
                .toList());
    }
}
