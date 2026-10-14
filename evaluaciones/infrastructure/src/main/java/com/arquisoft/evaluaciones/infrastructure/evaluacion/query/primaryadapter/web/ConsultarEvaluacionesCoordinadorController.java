package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesCoordinadorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto.EvaluacionResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper.ConsultarEvaluacionesCoordinadorRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper.EvaluacionResponseMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluacion.base:/evaluaciones}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.Evaluacion.TAG_NAME,
        description = EvaluacionesApiMessages.Evaluacion.TAG_DESCRIPTION)
public class ConsultarEvaluacionesCoordinadorController {

    private final ConsultarEvaluacionesCoordinadorInteractor consultarEvaluacionesCoordinadorInteractor;

    @PostMapping("${rutas.evaluaciones.evaluacion.coordinador:/coordinador}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_COORDINADOR_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.Evaluacion.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.Evaluacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.Evaluacion.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.Evaluacion.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<EvaluacionResponseDTO>> consultarEvaluacionesCoordinador(
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var query = ConsultarEvaluacionesCoordinadorRequestMapper.toQuery(request);
        var resultado = consultarEvaluacionesCoordinadorInteractor.ejecutar(query);

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EvaluacionResponseMapper::toResponse)));
    }
}
