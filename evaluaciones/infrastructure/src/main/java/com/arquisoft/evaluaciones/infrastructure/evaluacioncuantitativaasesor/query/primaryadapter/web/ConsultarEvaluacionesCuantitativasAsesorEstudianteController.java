package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.dto.EvaluacionCuantitativaAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.mapper.ConsultarEvaluacionesCuantitativasAsesorEstudianteRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.mapper.EvaluacionCuantitativaAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones.base:/evaluaciones/evaluaciones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_DESCRIPTION)
public class ConsultarEvaluacionesCuantitativasAsesorEstudianteController {

    private final ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor interactor;

    @GetMapping("${rutas.evaluaciones.evaluaciones.cuantitativas-estudiante:/{evaluacionAsesorId}/cuantitativas/estudiante}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            schema = @Schema(implementation = EvaluacionCuantitativaAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.CONSULTAR_ESTUDIANTE_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EvaluacionCuantitativaAsesorResponseDTO>>
            consultarEvaluacionesCuantitativasAsesorEstudiante(
            @PathVariable UUID evaluacionAsesorId,
            IdentidadActual identidad) {

        var estudiante = identidad.id();
        var query = ConsultarEvaluacionesCuantitativasAsesorEstudianteRequestMapper.toQuery(
                evaluacionAsesorId, estudiante);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(EvaluacionCuantitativaAsesorResponseMapper::toResponse)
                .toList());
    }
}
