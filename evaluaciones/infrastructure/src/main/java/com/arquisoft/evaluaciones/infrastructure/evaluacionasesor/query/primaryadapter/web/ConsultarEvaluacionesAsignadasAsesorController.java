package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionesAsignadasAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsignadaAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper.ConsultarEvaluacionesAsignadasAsesorRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper.EvaluacionAsignadaAsesorResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.evaluaciones.base:/evaluaciones/evaluaciones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionAsesor.TAG_DESCRIPTION)
public class ConsultarEvaluacionesAsignadasAsesorController {

    private final ConsultarEvaluacionesAsignadasAsesorInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones.asesor:/asesor}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_ASIGNADAS_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_ASIGNADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_ASIGNADAS_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.CONSULTAR_ASIGNADAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<EvaluacionAsignadaAsesorResponseDTO>> consultarEvaluacionesAsignadasAsesor(
            IdentidadActual identidad,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var query = ConsultarEvaluacionesAsignadasAsesorRequestMapper.toQuery(request, identidad.id());
        var resultado = interactor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EvaluacionAsignadaAsesorResponseMapper::toResponse)));
    }
}
