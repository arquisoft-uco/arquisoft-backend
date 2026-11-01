package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.CambiarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.CambiarVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper.CambiarVersionRepositorioArtefactoRequestMapper;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.Artefacto.TAG_NAME,
        description = ArtefactosApiMessages.Artefacto.TAG_DESCRIPTION)
public class CambiarVersionRepositorioArtefactoController {

    private final CambiarVersionRepositorioArtefactoInteractor cambiarVersionRepositorioArtefactoInteractor;

    @PatchMapping("${rutas.artefactos.artefactos.cambiar-version-repositorio:/{artefactoId}/version-repositorio}")
    @Operation(
            summary = ArtefactosApiMessages.Artefacto.CAMBIAR_VERSION_REPOSITORIO_SUMMARY,
            description = ArtefactosApiMessages.Artefacto.CAMBIAR_VERSION_REPOSITORIO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.Artefacto.CAMBIAR_VERSION_REPOSITORIO_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Artefacto.CAMBIAR_VERSION_REPOSITORIO_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Artefacto.CAMBIAR_VERSION_REPOSITORIO_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarVersionRepositorio(
            @PathVariable UUID artefactoId,
            @RequestBody CambiarVersionRepositorioArtefactoRequestDTO request,
            IdentidadActual identidad) {
        cambiarVersionRepositorioArtefactoInteractor.ejecutar(
                CambiarVersionRepositorioArtefactoRequestMapper.toCommand(artefactoId, identidad.id(), request));
        return ResponseEntity.noContent().build();
    }
}
