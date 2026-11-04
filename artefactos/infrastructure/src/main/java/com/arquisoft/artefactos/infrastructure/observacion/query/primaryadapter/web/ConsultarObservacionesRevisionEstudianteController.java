package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.ConsultarObservacionesRevisionEstudianteInteractor;
import com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.dto.ObservacionResponseDTO;
import com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper.ConsultarObservacionesRevisionEstudianteRequestMapper;
import com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper.ObservacionResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.observaciones.base:/revisiones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.Observacion.TAG_NAME,
        description = ArtefactosApiMessages.Observacion.TAG_DESCRIPTION)
public class ConsultarObservacionesRevisionEstudianteController {

    private final ConsultarObservacionesRevisionEstudianteInteractor consultarObservacionesRevisionEstudianteInteractor;

    @GetMapping("${rutas.artefactos.observaciones.listar-estudiante:/{revisionAsesorId}/observaciones/estudiante}")
    @Operation(
            summary = ArtefactosApiMessages.Observacion.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = ArtefactosApiMessages.Observacion.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.Observacion.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ObservacionResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Observacion.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Observacion.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<List<ObservacionResponseDTO>> consultarObservacionesDeLaRevisionComoEstudiante(
            @PathVariable("revisionAsesorId") UUID revisionAsesorId,
            IdentidadActual identidad) {

        var estudiante = identidad.id();
        var observaciones = consultarObservacionesRevisionEstudianteInteractor.ejecutar(
                ConsultarObservacionesRevisionEstudianteRequestMapper.toQuery(revisionAsesorId, estudiante));

        return ResponseEntity.ok(observaciones.stream()
                .map(ObservacionResponseMapper::toResponse)
                .toList());
    }
}
