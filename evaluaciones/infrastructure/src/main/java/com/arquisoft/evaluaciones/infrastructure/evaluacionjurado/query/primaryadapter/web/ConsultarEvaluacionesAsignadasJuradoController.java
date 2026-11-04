package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesAsignadasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto.EvaluacionAsignadaJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.ConsultarEvaluacionesAsignadasJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.EvaluacionAsignadaJuradoResponseMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-jurado.base:/evaluaciones/evaluaciones-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionJurado.TAG_DESCRIPTION)
public class ConsultarEvaluacionesAsignadasJuradoController {

    private final ConsultarEvaluacionesAsignadasJuradoInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-jurado.jurado:/jurado}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_ASIGNADAS_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
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
    public ResponseEntity<PageResponseDTO<EvaluacionAsignadaJuradoResponseDTO>> consultarEvaluacionesAsignadasJurado(
            IdentidadActual identidad,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var query = ConsultarEvaluacionesAsignadasJuradoRequestMapper.toQuery(request, identidad.id());
        var resultado = interactor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EvaluacionAsignadaJuradoResponseMapper::toResponse)));
    }
}
