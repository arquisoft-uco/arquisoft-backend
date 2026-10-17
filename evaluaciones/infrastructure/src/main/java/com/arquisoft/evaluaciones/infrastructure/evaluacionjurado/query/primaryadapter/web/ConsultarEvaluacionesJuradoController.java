package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.dto.EvaluacionJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.ConsultarEvaluacionesJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper.EvaluacionJuradoResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluacion.base:/evaluaciones}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionJurado.TAG_DESCRIPTION)
public class ConsultarEvaluacionesJuradoController {

    private final ConsultarEvaluacionesJuradoInteractor consultarEvaluacionesJuradoInteractor;

    @PostMapping("${rutas.evaluaciones.evaluacion.evaluaciones-jurado:/{evaluacionId}/evaluaciones-jurado}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_JURADO_ESTUDIANTE_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionJurado.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<EvaluacionJuradoResponseDTO>> consultarEvaluacionesJurado(
            @PathVariable UUID evaluacionId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var query = ConsultarEvaluacionesJuradoRequestMapper.toQuery(request, evaluacionId);
        var resultado = consultarEvaluacionesJuradoInteractor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EvaluacionJuradoResponseMapper::toResponse)));
    }
}
