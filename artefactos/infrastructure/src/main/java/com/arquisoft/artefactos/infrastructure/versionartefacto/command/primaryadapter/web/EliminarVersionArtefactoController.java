package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.EliminarVersionArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper.EliminarVersionArtefactoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.VersionArtefacto.TAG_NAME,
        description = ArtefactosApiMessages.VersionArtefacto.TAG_DESCRIPTION)
public class EliminarVersionArtefactoController {

    private final EliminarVersionArtefactoInteractor eliminarVersionArtefactoInteractor;

    @DeleteMapping("${rutas.artefactos.artefactos.version:/versiones/{versionArtefactoId}}")
    @Operation(
            summary = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_SUMMARY,
            description = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = ArtefactosApiMessages.VersionArtefacto.ELIMINAR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = ArtefactosApiMessages.VersionArtefacto.PARAM_VERSION)
            @PathVariable UUID versionArtefactoId,
            IdentidadActual identidad) {
        eliminarVersionArtefactoInteractor.ejecutar(
                EliminarVersionArtefactoRequestMapper.toCommand(versionArtefactoId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
