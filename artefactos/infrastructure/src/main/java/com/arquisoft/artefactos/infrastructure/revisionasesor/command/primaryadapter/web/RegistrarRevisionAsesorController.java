package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.RegistrarRevisionAsesorInteractor;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.dto.RegistrarRevisionAsesorResponseDTO;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper.RegistrarRevisionAsesorRequestMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.versiones-artefacto.base:/versiones-artefacto}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.RevisionAsesor.TAG_NAME, description = ArtefactosApiMessages.RevisionAsesor.TAG_DESCRIPTION)
public class RegistrarRevisionAsesorController {

    private final RegistrarRevisionAsesorInteractor registrarRevisionAsesorInteractor;

    @PostMapping("${rutas.artefactos.versiones-artefacto.revisiones:/{versionArtefactoId}/revisiones}")
    @Operation(
            summary = ArtefactosApiMessages.RevisionAsesor.REGISTRAR_SUMMARY,
            description = ArtefactosApiMessages.RevisionAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = ArtefactosApiMessages.RevisionAsesor.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = RegistrarRevisionAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.RevisionAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.RevisionAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarRevisionAsesorResponseDTO> registrar(
            @PathVariable UUID versionArtefactoId,
            IdentidadActual identidad) {
        var id = registrarRevisionAsesorInteractor.ejecutar(
                RegistrarRevisionAsesorRequestMapper.toCommand(versionArtefactoId, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarRevisionAsesorResponseDTO(id));
    }
}
