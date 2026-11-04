package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarParticipacionesArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.dto.ParticipacionArtefactoResponseDTO;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper.ConsultarParticipacionesArtefactoRequestMapper;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper.ParticipacionArtefactoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.EstudianteArtefacto.TAG_NAME,
        description = ArtefactosApiMessages.EstudianteArtefacto.TAG_DESCRIPTION)
public class ConsultarParticipacionesArtefactoController {

    private final ConsultarParticipacionesArtefactoInteractor consultarParticipacionesArtefactoInteractor;

    @GetMapping("${rutas.artefactos.artefactos.estudiante-participaciones:/estudiante/participaciones}")
    @Operation(
            summary = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_PARTICIPACION_SUMMARY,
            description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_PARTICIPACION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_PARTICIPACION_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipacionArtefactoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_PARTICIPACION_RESP_403)
    })
    public ResponseEntity<List<ParticipacionArtefactoResponseDTO>> consultarParticipaciones(
            IdentidadActual identidad) {

        var estudiante = identidad.id();
        var participaciones = consultarParticipacionesArtefactoInteractor.ejecutar(
                ConsultarParticipacionesArtefactoRequestMapper.toQuery(estudiante));

        return ResponseEntity.ok(participaciones.stream()
                .map(ParticipacionArtefactoResponseMapper::toResponse)
                .toList());
    }
}
