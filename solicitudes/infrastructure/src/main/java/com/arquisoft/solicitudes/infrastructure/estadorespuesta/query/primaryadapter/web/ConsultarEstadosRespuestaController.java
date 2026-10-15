package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web;

import com.arquisoft.solicitudes.application.estadorespuesta.query.primaryport.interactor.ConsultarEstadosRespuestaInteractor;
import com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web.dto.EstadoRespuestaResponseDTO;
import com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web.mapper.EstadoRespuestaResponseMapper;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.EstadoRespuesta.TAG_NAME,
        description = SolicitudesApiMessages.EstadoRespuesta.TAG_DESCRIPTION)
public class ConsultarEstadosRespuestaController {

    private final ConsultarEstadosRespuestaInteractor consultarEstadosRespuestaInteractor;

    @GetMapping("${rutas.solicitudes.respuesta.estados-respuesta:/estados-respuesta}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_ESTADO_RESPUESTA_VIEW)
    @Operation(
            summary = SolicitudesApiMessages.EstadoRespuesta.CONSULTAR_SUMMARY,
            description = SolicitudesApiMessages.EstadoRespuesta.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SolicitudesApiMessages.EstadoRespuesta.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoRespuestaResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Comun.RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoRespuestaResponseDTO>> consultarEstadosRespuesta() {
        var estados = consultarEstadosRespuestaInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoRespuestaResponseMapper::toResponse)
                .toList());
    }
}
