package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarEstudiantesArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.dto.EstudianteArtefactoResponseDTO;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper.ConsultarEstudiantesArtefactoRequestMapper;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper.EstudianteArtefactoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
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
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.EstudianteArtefacto.TAG_NAME,
        description = ArtefactosApiMessages.EstudianteArtefacto.TAG_DESCRIPTION)
public class ConsultarEstudiantesArtefactoController {

    private final ConsultarEstudiantesArtefactoInteractor consultarEstudiantesArtefactoInteractor;

    @GetMapping("${rutas.artefactos.artefactos.estudiantes:/{artefactoId}/estudiantes}")
    @Operation(
            summary = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_SUMMARY,
            description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstudianteArtefactoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.EstudianteArtefacto.CONSULTAR_RESP_403)
    })
    public ResponseEntity<List<EstudianteArtefactoResponseDTO>> consultarEstudiantesDelArtefacto(
            @PathVariable("artefactoId") UUID artefactoId) {

        var estudiantes = consultarEstudiantesArtefactoInteractor.ejecutar(
                ConsultarEstudiantesArtefactoRequestMapper.toQuery(artefactoId));

        return ResponseEntity.ok(estudiantes.stream()
                .map(EstudianteArtefactoResponseMapper::toResponse)
                .toList());
    }
}
