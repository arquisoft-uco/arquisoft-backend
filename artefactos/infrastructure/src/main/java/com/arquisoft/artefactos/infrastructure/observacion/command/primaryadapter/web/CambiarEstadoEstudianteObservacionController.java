package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoEstudianteObservacionInteractor;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.CambiarEstadoEstudianteObservacionRequestDTO;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper.CambiarEstadoEstudianteObservacionRequestMapper;
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
public class CambiarEstadoEstudianteObservacionController {

    private final CambiarEstadoEstudianteObservacionInteractor cambiarEstadoEstudianteObservacionInteractor;

    @PatchMapping("${rutas.artefactos.artefactos.estado-estudiante-observacion:/observaciones/{observacionId}/estado-estudiante}")
    @Operation(
            summary = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ESTUDIANTE_SUMMARY,
            description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ESTUDIANTE_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ESTUDIANTE_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Observacion.CAMBIAR_ESTADO_ESTUDIANTE_RESP_422)
    })
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable UUID observacionId,
            @RequestBody CambiarEstadoEstudianteObservacionRequestDTO request,
            IdentidadActual identidad) {

        cambiarEstadoEstudianteObservacionInteractor.ejecutar(
                CambiarEstadoEstudianteObservacionRequestMapper.toCommand(request, observacionId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
