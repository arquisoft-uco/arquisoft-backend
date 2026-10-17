package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SubirVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.security.RepositorioArtefactosAuthorities;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SubirVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SubirVersionRepositorioArtefactoResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper.SubirVersionRepositorioArtefactoRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper.SubirVersionRepositorioArtefactoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.RepositorioArtefactosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
public class SubirVersionRepositorioArtefactoController {

    private final SubirVersionRepositorioArtefactoInteractor interactor;

    @PostMapping("${rutas.repositorio-artefactos.repositorio-artefactos.versiones:/{repositorioId}/versiones}")
    @PreAuthorize(RepositorioArtefactosAuthorities.Expresiones.HAS_VERSION_REPOSITORIO_ARTEFACTO_CREATE)
    @Operation(
            summary = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_SUMMARY,
            description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = SubirVersionRepositorioArtefactoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.SUBIR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<SubirVersionRepositorioArtefactoResponseDTO> subir(
            @Parameter(description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.PARAM_REPOSITORIO)
            @PathVariable UUID repositorioId,
            @RequestBody SubirVersionRepositorioArtefactoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        var result = interactor.ejecutar(
                SubirVersionRepositorioArtefactoRequestMapper.toCommand(request, repositorioId, jwt.getSubject()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubirVersionRepositorioArtefactoResponseMapper.toResponse(result));
    }
}
