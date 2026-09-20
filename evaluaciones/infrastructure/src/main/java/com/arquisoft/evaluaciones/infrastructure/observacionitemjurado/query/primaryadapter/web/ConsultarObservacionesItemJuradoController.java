package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.interactor.ConsultarObservacionesItemJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.dto.ObservacionItemJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.mapper.ConsultarObservacionesItemJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.mapper.ObservacionItemJuradoResponseMapper;
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
@RequestMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.base:/evaluaciones/evaluaciones-cuantitativas-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemJurado.TAG_DESCRIPTION)
public class ConsultarObservacionesItemJuradoController {

    private final ConsultarObservacionesItemJuradoInteractor consultarObservacionesItemJuradoInteractor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.observaciones-jurado:/{id}/observaciones/jurado}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_OBSERVACION_ITEM_JURADO_JURADO_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<ObservacionItemJuradoResponseDTO>> consultarObservaciones(
            @PathVariable UUID id,
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var query = ConsultarObservacionesItemJuradoRequestMapper.toQuery(request, id);
        var resultado = consultarObservacionesItemJuradoInteractor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ObservacionItemJuradoResponseMapper::toResponse)));
    }
}
