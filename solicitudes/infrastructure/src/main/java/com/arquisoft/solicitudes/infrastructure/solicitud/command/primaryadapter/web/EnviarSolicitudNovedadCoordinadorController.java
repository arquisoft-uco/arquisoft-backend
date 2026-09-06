package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadCoordinadorRequestDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadCoordinadorResponseDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper.EnviarSolicitudNovedadCoordinadorRequestMapper;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Solicitud.TAG_NAME,
        description = SolicitudesApiMessages.Solicitud.TAG_DESCRIPTION)
public class EnviarSolicitudNovedadCoordinadorController {

    private final EnviarSolicitudNovedadCoordinadorInteractor enviarSolicitudNovedadCoordinadorInteractor;

    @PostMapping("${rutas.solicitudes.solicitud.novedad-coordinador:/novedad-coordinador}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CREATE)
    @Operation(
            summary = SolicitudesApiMessages.Solicitud.ENVIAR_NOVEDAD_COORDINADOR_SUMMARY,
            description = SolicitudesApiMessages.Solicitud.ENVIAR_NOVEDAD_COORDINADOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_NOVEDAD_COORDINADOR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_NOVEDAD_COORDINADOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Solicitud.ENVIAR_NOVEDAD_COORDINADOR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<EnviarSolicitudNovedadCoordinadorResponseDTO> enviar(
            @RequestBody EnviarSolicitudNovedadCoordinadorRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID id = enviarSolicitudNovedadCoordinadorInteractor.ejecutar(
                EnviarSolicitudNovedadCoordinadorRequestMapper.toCommand(request, jwt.getSubject()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EnviarSolicitudNovedadCoordinadorResponseDTO(id));
    }
}
