package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionEvaluacionesInteractor;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.mapper.EstadoEvaluacionResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.evaluaciones.estados-evaluacion.base:/evaluaciones/estados-evaluacion}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EstadoEvaluacion.TAG_NAME,
        description = EvaluacionesApiMessages.EstadoEvaluacion.TAG_DESCRIPTION)
public class ConsultarEstadosEvaluacionEvaluacionesController {

    private final ConsultarEstadosEvaluacionEvaluacionesInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ESTADO_EVALUACION_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.EstadoEvaluacion.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.EstadoEvaluacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EstadoEvaluacion.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoEvaluacionResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<EstadoEvaluacionResponseDTO>> consultarEstadosEvaluacion() {
        List<EstadoEvaluacionReadModel> estados = interactor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoEvaluacionResponseMapper::toResponse)
                .toList());
    }
}
