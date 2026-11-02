package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.DescargarArchivoVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.EnlaceDescargaVersionResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.DescargarArchivoVersionRepositorioArtefactoRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.EnlaceDescargaVersionResponseMapper;
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
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_DESCRIPTION)
public class DescargarArchivoVersionRepositorioArtefactoAdministradorController {

    private final DescargarArchivoVersionRepositorioArtefactoInteractor interactor;

    @GetMapping("${rutas.repositorio-artefactos.repositorio-artefactos.descarga-version-administrador:"
            + "/versiones/administrador/{versionRepositorioArtefactoId}/descarga}")
    @Operation(
            summary = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_ADMINISTRADOR_SUMMARY,
            description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_ADMINISTRADOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EnlaceDescargaVersionResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.DESCARGAR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<EnlaceDescargaVersionResponseDTO> descargar(
            @Parameter(description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.PARAM_VERSION)
            @PathVariable UUID versionRepositorioArtefactoId) {
        var resultado = interactor.ejecutar(
                DescargarArchivoVersionRepositorioArtefactoRequestMapper.toQuery(versionRepositorioArtefactoId));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(EnlaceDescargaVersionResponseMapper.toResponse(resultado));
    }
}
