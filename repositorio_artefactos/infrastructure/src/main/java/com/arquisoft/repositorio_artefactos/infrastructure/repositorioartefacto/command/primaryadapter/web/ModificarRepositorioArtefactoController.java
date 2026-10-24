package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.ModificarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.ModificarRepositorioArtefactoRequestDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper.ModificarRepositorioArtefactoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.RepositorioArtefactosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.RepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.RepositorioArtefacto.TAG_DESCRIPTION)
public class ModificarRepositorioArtefactoController {

    private final ModificarRepositorioArtefactoInteractor interactor;

    @PatchMapping("${rutas.repositorio-artefactos.repositorio-artefactos.por-id:/{repositorioId}}")
    @Operation(
            summary = RepositorioArtefactosApiMessages.RepositorioArtefacto.MODIFICAR_SUMMARY,
            description = RepositorioArtefactosApiMessages.RepositorioArtefacto.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @Parameter(description = RepositorioArtefactosApiMessages.RepositorioArtefacto.PARAM_REPOSITORIO)
            @PathVariable UUID repositorioId,
            @RequestBody ModificarRepositorioArtefactoRequestDTO request,
            IdentidadActual identidad) {
        interactor.ejecutar(ModificarRepositorioArtefactoRequestMapper.toCommand(
                request, repositorioId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
