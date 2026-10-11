package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionCoordinadorInteractor;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.dto.ObservacionEvaluacionResponseDTO;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper.ConsultarObservacionesEvaluacionCoordinadorRequestMapper;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper.ObservacionEvaluacionResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ObservacionEvaluacion.TAG_NAME,
        description = FichasApiMessages.ObservacionEvaluacion.TAG_DESCRIPTION)
public class ConsultarObservacionesEvaluacionCoordinadorController {

    private final ConsultarObservacionesEvaluacionCoordinadorInteractor consultarObservacionesEvaluacionCoordinadorInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.observaciones-evaluacion-coordinador:"
            + "/{fichaPerfilId}/observaciones-evaluacion/coordinador}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_EVALUACION_COORDINADOR_VIEW)
    @Operation(
            summary = FichasApiMessages.ObservacionEvaluacion.CONSULTAR_COORDINADOR_SUMMARY,
            description = FichasApiMessages.ObservacionEvaluacion.CONSULTAR_COORDINADOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.ObservacionEvaluacion.CONSULTAR_COORDINADOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ObservacionEvaluacionResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionEvaluacion.CONSULTAR_COORDINADOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionEvaluacion.CONSULTAR_COORDINADOR_RESP_403)
    })
    public ResponseEntity<List<ObservacionEvaluacionResponseDTO>> consultarObservaciones(
            @PathVariable UUID fichaPerfilId) {

        var resultado = consultarObservacionesEvaluacionCoordinadorInteractor.ejecutar(
                ConsultarObservacionesEvaluacionCoordinadorRequestMapper.toQuery(fichaPerfilId));

        return ResponseEntity.ok(resultado.stream()
                .map(ObservacionEvaluacionResponseMapper::toResponse)
                .toList());
    }
}
