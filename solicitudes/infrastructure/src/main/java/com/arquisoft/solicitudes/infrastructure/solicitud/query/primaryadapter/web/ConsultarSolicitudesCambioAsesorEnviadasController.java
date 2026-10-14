package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesCambioAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.dto.SolicitudResponseDTO;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper.ConsultarSolicitudesCambioAsesorEnviadasRequestMapper;
import com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper.SolicitudResponseMapper;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Solicitud.TAG_NAME,
        description = SolicitudesApiMessages.Solicitud.TAG_DESCRIPTION)
public class ConsultarSolicitudesCambioAsesorEnviadasController {

    private final ConsultarSolicitudesCambioAsesorEnviadasInteractor
            consultarSolicitudesCambioAsesorEnviadasInteractor;

    @PostMapping(
            "${rutas.solicitudes.solicitud.cambio-asesor-enviadas:/cambio-asesor/enviadas}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CAMBIO_ASESOR_ENVIADA_VIEW)
    @Operation(
            summary = SolicitudesApiMessages.Solicitud.CONSULTAR_CAMBIO_ASESOR_ENVIADAS_SUMMARY,
            description = SolicitudesApiMessages.Solicitud.CONSULTAR_CAMBIO_ASESOR_ENVIADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SolicitudesApiMessages.Solicitud.CONSULTAR_CAMBIO_ASESOR_ENVIADAS_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Solicitud.CONSULTAR_CAMBIO_ASESOR_ENVIADAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Solicitud.CONSULTAR_CAMBIO_ASESOR_ENVIADAS_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<SolicitudResponseDTO>> consultarCambioAsesorEnviadas(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var estudianteUsuario = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarSolicitudesCambioAsesorEnviadasInteractor.ejecutar(
                ConsultarSolicitudesCambioAsesorEnviadasRequestMapper.toQuery(
                        request, estudianteUsuario));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(SolicitudResponseMapper::toResponse)));
    }
}
