package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudRegistroModificacionUsuariosRequestDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudRegistroModificacionUsuariosResponseDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper.EnviarSolicitudRegistroModificacionUsuariosRequestMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Solicitud.TAG_NAME,
        description = SolicitudesApiMessages.Solicitud.TAG_DESCRIPTION)
public class EnviarSolicitudRegistroModificacionUsuariosController {

    private final EnviarSolicitudRegistroModificacionUsuariosInteractor enviarSolicitudRegistroModificacionUsuariosInteractor;

    @PostMapping("${rutas.solicitudes.solicitud.registro-modificacion-usuarios:/registro-modificacion-usuarios}")
    @Operation(
            summary = SolicitudesApiMessages.Solicitud.ENVIAR_REGISTRO_MODIFICACION_USUARIOS_SUMMARY,
            description = SolicitudesApiMessages.Solicitud.ENVIAR_REGISTRO_MODIFICACION_USUARIOS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_REGISTRO_MODIFICACION_USUARIOS_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_REGISTRO_MODIFICACION_USUARIOS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_REGISTRO_MODIFICACION_USUARIOS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<EnviarSolicitudRegistroModificacionUsuariosResponseDTO> enviar(
            @RequestBody EnviarSolicitudRegistroModificacionUsuariosRequestDTO request,
            IdentidadActual identidad) {

        var remitenteUsuario = identidad.id();

        var id = enviarSolicitudRegistroModificacionUsuariosInteractor.ejecutar(
                EnviarSolicitudRegistroModificacionUsuariosRequestMapper.toCommand(request, remitenteUsuario));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EnviarSolicitudRegistroModificacionUsuariosResponseDTO(id));
    }
}
