package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.RemoverEstudianteArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper.RemoverEstudianteArtefactoRequestMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.EstudianteArtefacto.TAG_NAME,
        description = ArtefactosApiMessages.EstudianteArtefacto.TAG_DESCRIPTION)
public class RemoverEstudianteArtefactoController {

    private final RemoverEstudianteArtefactoInteractor removerEstudianteArtefactoInteractor;

    @DeleteMapping("${rutas.artefactos.artefactos.estudiante:/{artefactoId}/estudiantes/{estudianteId}}")
    @Operation(
            summary = ArtefactosApiMessages.EstudianteArtefacto.REMOVER_SUMMARY,
            description = ArtefactosApiMessages.EstudianteArtefacto.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.EstudianteArtefacto.REMOVER_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.EstudianteArtefacto.REMOVER_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.EstudianteArtefacto.REMOVER_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> removerEstudiante(
            @PathVariable("artefactoId") UUID artefactoId,
            @PathVariable("estudianteId") UUID estudianteId,
            IdentidadActual identidad) {
        removerEstudianteArtefactoInteractor.ejecutar(
                RemoverEstudianteArtefactoRequestMapper.toCommand(artefactoId, estudianteId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
