package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper.ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class ModificarEstadoRespuestaRegistroModificacionUsuariosController {

    private final ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor
            modificarEstadoRespuestaRegistroModificacionUsuariosInteractor;

    @PatchMapping(
            "${rutas.solicitudes.respuesta.registro-modificacion-usuarios-estado:"
                    + "/registro-modificacion-usuarios/{solicitudId}/respuesta/estado}")
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Respuesta.MODIFICAR_ESTADO_REGISTRO_MODIFICACION_USUARIOS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificarEstado(
            @PathVariable UUID solicitudId,
            @RequestBody ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO request,
            IdentidadActual identidad) {

        var administradorUsuario = identidad.id();
        modificarEstadoRespuestaRegistroModificacionUsuariosInteractor.ejecutar(
                ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapper.toCommand(
                        request, solicitudId, administradorUsuario));

        return ResponseEntity.noContent().build();
    }
}
