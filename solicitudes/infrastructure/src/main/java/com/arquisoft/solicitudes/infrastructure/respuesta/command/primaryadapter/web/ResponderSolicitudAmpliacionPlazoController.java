package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudAmpliacionPlazoRequestDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudAmpliacionPlazoResponseDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper.ResponderSolicitudAmpliacionPlazoRequestMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class ResponderSolicitudAmpliacionPlazoController {

    private final ResponderSolicitudAmpliacionPlazoInteractor responderSolicitudAmpliacionPlazoInteractor;

    @PostMapping(
            "${rutas.solicitudes.respuesta.ampliacion-plazo:/ampliacion-plazo/{solicitudId}/respuesta}")
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = SolicitudesApiMessages.Respuesta.RESPONDER_AMPLIACION_PLAZO_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ResponderSolicitudAmpliacionPlazoResponseDTO> responder(
            @PathVariable String solicitudId,
            @RequestBody ResponderSolicitudAmpliacionPlazoRequestDTO request,
            IdentidadActual identidad) {

        var coordinadorUsuario = identidad.id();
        var id = responderSolicitudAmpliacionPlazoInteractor.ejecutar(
                ResponderSolicitudAmpliacionPlazoRequestMapper.toCommand(
                        request, solicitudId, coordinadorUsuario));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponderSolicitudAmpliacionPlazoResponseDTO(id));
    }
}
