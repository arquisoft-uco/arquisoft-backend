package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.dto.EvaluacionCuantitativaJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper.ConsultarEvaluacionesCuantitativasJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper.EvaluacionCuantitativaJuradoResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
        name = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_DESCRIPTION)
public class ConsultarEvaluacionesCuantitativasJuradoController {

    private final ConsultarEvaluacionesCuantitativasJuradoInteractor interactor;

    @GetMapping("${rutas.evaluaciones.evaluaciones-jurado.cuantitativas:/{evaluacionJuradoId}/cuantitativas}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_CUANTITATIVA_JURADO_ESTUDIANTE_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            schema = @Schema(implementation = EvaluacionCuantitativaJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EvaluacionCuantitativaJuradoResponseDTO>> consultarEvaluacionesCuantitativasJurado(
            @PathVariable UUID evaluacionJuradoId,
            @AuthenticationPrincipal Jwt jwt) {

        var query = ConsultarEvaluacionesCuantitativasJuradoRequestMapper.toQuery(evaluacionJuradoId, jwt.getSubject());

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(EvaluacionCuantitativaJuradoResponseMapper::toResponse)
                .toList());
    }
}
