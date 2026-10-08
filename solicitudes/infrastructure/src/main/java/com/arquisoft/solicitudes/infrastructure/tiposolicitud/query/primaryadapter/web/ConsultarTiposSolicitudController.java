package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web;

import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudInteractor;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.dto.TipoSolicitudResponseDTO;
import com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper.TipoSolicitudResponseMapper;
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
@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.TipoSolicitud.TAG_NAME,
        description = SolicitudesApiMessages.TipoSolicitud.TAG_DESCRIPTION)
public class ConsultarTiposSolicitudController {

    private final ConsultarTiposSolicitudInteractor consultarTiposSolicitudInteractor;

    @GetMapping("${rutas.solicitudes.solicitud.tipos-solicitud:/tipos-solicitud}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_TIPO_SOLICITUD_VIEW)
    @Operation(
            summary = SolicitudesApiMessages.TipoSolicitud.CONSULTAR_SUMMARY,
            description = SolicitudesApiMessages.TipoSolicitud.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SolicitudesApiMessages.TipoSolicitud.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TipoSolicitudResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Comun.RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<TipoSolicitudResponseDTO>> consultarTiposSolicitud() {
        var tipos = consultarTiposSolicitudInteractor.ejecutar();

        return ResponseEntity.ok(tipos.stream()
                .map(TipoSolicitudResponseMapper::toResponse)
                .toList());
    }
}
