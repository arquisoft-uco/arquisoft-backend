package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudNovedadCoordinadorRequestDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudNovedadCoordinadorResponseDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper.ResponderSolicitudNovedadCoordinadorRequestMapper;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class ResponderSolicitudNovedadCoordinadorController {

    private final ResponderSolicitudNovedadCoordinadorInteractor responderSolicitudNovedadCoordinadorInteractor;

    @PostMapping(
            "${rutas.solicitudes.respuesta.novedad-coordinador:/novedad-coordinador/{solicitudId}/respuesta}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_RESPUESTA_NOVEDAD_COORDINADOR_CREATE)
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_NOVEDAD_COORDINADOR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ResponderSolicitudNovedadCoordinadorResponseDTO> responder(
            @PathVariable String solicitudId,
            @RequestBody ResponderSolicitudNovedadCoordinadorRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID id = responderSolicitudNovedadCoordinadorInteractor.ejecutar(
                ResponderSolicitudNovedadCoordinadorRequestMapper.toCommand(
                        request, solicitudId, jwt.getSubject()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponderSolicitudNovedadCoordinadorResponseDTO(id));
    }
}
