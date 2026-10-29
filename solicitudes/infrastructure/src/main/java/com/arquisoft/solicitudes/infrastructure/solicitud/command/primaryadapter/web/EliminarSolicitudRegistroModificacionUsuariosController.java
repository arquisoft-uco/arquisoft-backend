package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Solicitud.TAG_NAME,
        description = SolicitudesApiMessages.Solicitud.TAG_DESCRIPTION)
public class EliminarSolicitudRegistroModificacionUsuariosController {

    private final EliminarSolicitudRegistroModificacionUsuariosInteractor eliminarSolicitudRegistroModificacionUsuariosInteractor;

    @DeleteMapping(
            "${rutas.solicitudes.solicitud.registro-modificacion-usuarios-por-id:/registro-modificacion-usuarios/{solicitudId}}")
    @Operation(
            summary = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_SUMMARY,
            description = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Solicitud.ELIMINAR_REGISTRO_MODIFICACION_USUARIOS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable String solicitudId,
                                         IdentidadActual identidad) {

        var remitenteUsuario = identidad.id();

        eliminarSolicitudRegistroModificacionUsuariosInteractor.ejecutar(
                EliminarSolicitudRegistroModificacionUsuariosCommand.crear(solicitudId, remitenteUsuario));

        return ResponseEntity.noContent().build();
    }
}
