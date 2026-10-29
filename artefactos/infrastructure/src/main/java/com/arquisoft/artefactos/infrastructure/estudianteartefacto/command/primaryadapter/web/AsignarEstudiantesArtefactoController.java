package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.AsignarEstudiantesArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.dto.AsignarEstudiantesArtefactoRequestDTO;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper.AsignarEstudiantesArtefactoRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.EstudianteArtefacto.TAG_NAME,
        description = ArtefactosApiMessages.EstudianteArtefacto.TAG_DESCRIPTION)
public class AsignarEstudiantesArtefactoController {

    private final AsignarEstudiantesArtefactoInteractor asignarEstudiantesArtefactoInteractor;

    @PostMapping("${rutas.artefactos.artefactos.estudiantes:/{artefactoId}/estudiantes}")
    @Operation(
            summary = ArtefactosApiMessages.EstudianteArtefacto.ASIGNAR_SUMMARY,
            description = ArtefactosApiMessages.EstudianteArtefacto.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = ArtefactosApiMessages.EstudianteArtefacto.ASIGNAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.EstudianteArtefacto.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.EstudianteArtefacto.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable("artefactoId") UUID artefactoId,
            @RequestBody AsignarEstudiantesArtefactoRequestDTO request,
            IdentidadActual identidad) {
        asignarEstudiantesArtefactoInteractor.ejecutar(
                AsignarEstudiantesArtefactoRequestMapper.toCommand(artefactoId, request, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
