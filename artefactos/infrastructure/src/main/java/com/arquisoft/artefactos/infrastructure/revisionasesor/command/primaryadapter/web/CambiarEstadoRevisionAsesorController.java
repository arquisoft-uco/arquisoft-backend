package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.CambiarEstadoRevisionAsesorInteractor;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.dto.CambiarEstadoRevisionAsesorRequestDTO;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper.CambiarEstadoRevisionAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("${rutas.artefactos.revisiones-asesor.base:/revisiones-asesor}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.RevisionAsesor.TAG_NAME, description = ArtefactosApiMessages.RevisionAsesor.TAG_DESCRIPTION)
public class CambiarEstadoRevisionAsesorController {

    private final CambiarEstadoRevisionAsesorInteractor cambiarEstadoRevisionAsesorInteractor;

    @PatchMapping("${rutas.artefactos.revisiones-asesor.estado:/{revisionAsesorId}/estado}")
    @Operation(
            summary = ArtefactosApiMessages.RevisionAsesor.CAMBIAR_ESTADO_SUMMARY,
            description = ArtefactosApiMessages.RevisionAsesor.CAMBIAR_ESTADO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.RevisionAsesor.CAMBIAR_ESTADO_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.RevisionAsesor.CAMBIAR_ESTADO_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.RevisionAsesor.CAMBIAR_ESTADO_RESP_422)
    })
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID revisionAsesorId,
            @RequestBody CambiarEstadoRevisionAsesorRequestDTO request,
            IdentidadActual identidad) {

        cambiarEstadoRevisionAsesorInteractor.ejecutar(
                CambiarEstadoRevisionAsesorRequestMapper.toCommand(request, revisionAsesorId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
