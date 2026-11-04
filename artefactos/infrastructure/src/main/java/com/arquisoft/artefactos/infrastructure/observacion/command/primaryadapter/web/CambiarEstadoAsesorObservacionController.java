package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoAsesorObservacionInteractor;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.CambiarEstadoAsesorObservacionRequestDTO;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper.CambiarEstadoAsesorObservacionRequestMapper;
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
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.Observacion.TAG_NAME, description = ArtefactosApiMessages.Observacion.TAG_DESCRIPTION)
public class CambiarEstadoAsesorObservacionController {

    private final CambiarEstadoAsesorObservacionInteractor cambiarEstadoAsesorObservacionInteractor;

    @PatchMapping("${rutas.artefactos.artefactos.estado-asesor-observacion:/observaciones/{observacionId}/estado-asesor}")
    @Operation(
            summary = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ASESOR_SUMMARY,
            description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ASESOR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ASESOR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ASESOR_RESP_422)
    })
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID observacionId,
            @RequestBody CambiarEstadoAsesorObservacionRequestDTO request,
            IdentidadActual identidad) {

        cambiarEstadoAsesorObservacionInteractor.ejecutar(
                CambiarEstadoAsesorObservacionRequestMapper.toCommand(request, observacionId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
