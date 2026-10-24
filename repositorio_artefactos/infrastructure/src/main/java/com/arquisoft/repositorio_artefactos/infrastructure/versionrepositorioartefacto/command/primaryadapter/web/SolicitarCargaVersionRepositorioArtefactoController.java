package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SolicitarCargaVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionRepositorioArtefactoResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper.SolicitarCargaVersionRepositorioArtefactoRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper.SolicitarCargaVersionRepositorioArtefactoResponseMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_DESCRIPTION)
public class SolicitarCargaVersionRepositorioArtefactoController {

    private final SolicitarCargaVersionRepositorioArtefactoInteractor interactor;

    @PostMapping("${rutas.repositorio-artefactos.repositorio-artefactos.cargas-version:/{repositorioId}/versiones/cargas}")
    @Operation(
            summary = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_SUMMARY,
            description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_RESP_200,
                    content = @Content(schema = @Schema(
                            implementation = SolicitarCargaVersionRepositorioArtefactoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SOLICITAR_CARGA_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<SolicitarCargaVersionRepositorioArtefactoResponseDTO> solicitar(
            @Parameter(description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.PARAM_REPOSITORIO)
            @PathVariable UUID repositorioId,
            @RequestBody SolicitarCargaVersionRepositorioArtefactoRequestDTO request,
            IdentidadActual identidad) {
        var result = interactor.ejecutar(SolicitarCargaVersionRepositorioArtefactoRequestMapper.toCommand(
                request, repositorioId, identidad.id().toString()));
        return ResponseEntity.ok(SolicitarCargaVersionRepositorioArtefactoResponseMapper.toResponse(result));
    }
}
