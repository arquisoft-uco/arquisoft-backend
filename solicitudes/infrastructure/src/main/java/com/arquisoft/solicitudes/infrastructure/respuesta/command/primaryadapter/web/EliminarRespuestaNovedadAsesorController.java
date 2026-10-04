package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class EliminarRespuestaNovedadAsesorController {

    private final EliminarRespuestaNovedadAsesorInteractor eliminarRespuestaNovedadAsesorInteractor;

    @DeleteMapping(
            "${rutas.solicitudes.respuesta.novedad-asesor:/novedad-asesor/{solicitudId}/respuesta}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_RESPUESTA_NOVEDAD_ASESOR_DELETE)
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Respuesta.ELIMINAR_NOVEDAD_ASESOR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable String solicitudId,
                                         @AuthenticationPrincipal Jwt jwt) {

        var asesorUsuario = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        eliminarRespuestaNovedadAsesorInteractor.ejecutar(
                EliminarRespuestaNovedadAsesorCommand.crear(solicitudId, asesorUsuario));

        return ResponseEntity.noContent().build();
    }
}
