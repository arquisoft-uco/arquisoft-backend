package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionInteractor;
import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionResponseDTO;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.mapper.EstadoEvaluacionResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ApiCodes;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstadoEvaluacion.TAG_NAME, description = FichasApiMessages.EstadoEvaluacion.TAG_DESCRIPTION)
public class ConsultarEstadosEvaluacionController {

    private final ConsultarEstadosEvaluacionInteractor consultarEstadosEvaluacionInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-evaluacion:/estados-evaluacion}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_EVALUACION_VIEW)
    @Operation(
            summary = FichasApiMessages.EstadoEvaluacion.CONSULTAR_SUMMARY,
            description = FichasApiMessages.EstadoEvaluacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EstadoEvaluacion.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoEvaluacionResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.EstadoEvaluacion.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoEvaluacion.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoEvaluacionResponseDTO>> consultarEstadosEvaluacion() {
        List<EstadoEvaluacionReadModel> estados = consultarEstadosEvaluacionInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoEvaluacionResponseMapper::toResponse)
                .toList());
    }
}
