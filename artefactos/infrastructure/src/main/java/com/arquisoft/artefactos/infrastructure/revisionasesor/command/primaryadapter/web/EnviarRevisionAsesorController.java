package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.EnviarRevisionAsesorInteractor;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper.EnviarRevisionAsesorRequestMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.RevisionAsesor.TAG_NAME, description = ArtefactosApiMessages.RevisionAsesor.TAG_DESCRIPTION)
public class EnviarRevisionAsesorController {

    private final EnviarRevisionAsesorInteractor enviarRevisionAsesorInteractor;

    @PatchMapping("${rutas.artefactos.artefactos.envio-revision:/revisiones/{revisionAsesorId}/envio}")
    @Operation(
            summary = ArtefactosApiMessages.RevisionAsesor.ENVIAR_SUMMARY,
            description = ArtefactosApiMessages.RevisionAsesor.ENVIAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.RevisionAsesor.ENVIAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.RevisionAsesor.ENVIAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.RevisionAsesor.ENVIAR_RESP_422)
    })
    public ResponseEntity<Void> enviar(@PathVariable UUID revisionAsesorId, IdentidadActual identidad) {
        enviarRevisionAsesorInteractor.ejecutar(
                EnviarRevisionAsesorRequestMapper.toCommand(revisionAsesorId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
