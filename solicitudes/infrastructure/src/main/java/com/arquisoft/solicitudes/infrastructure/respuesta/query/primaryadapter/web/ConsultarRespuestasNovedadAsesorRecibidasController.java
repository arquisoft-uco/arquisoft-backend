package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasNovedadAsesorRecibidasInteractor;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.dto.RespuestaResponseDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper.ConsultarRespuestasNovedadAsesorRecibidasRequestMapper;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper.RespuestaResponseMapper;
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
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class ConsultarRespuestasNovedadAsesorRecibidasController {

    private final ConsultarRespuestasNovedadAsesorRecibidasInteractor
            consultarRespuestasNovedadAsesorRecibidasInteractor;

    @PostMapping(
            "${rutas.solicitudes.respuesta.novedad-asesor-recibidas:"
                    + "/novedad-asesor/respuestas/recibidas}")
    @PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_RESPUESTA_NOVEDAD_ASESOR_RECIBIDA_VIEW)
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.CONSULTAR_NOVEDAD_ASESOR_RECIBIDAS_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.CONSULTAR_NOVEDAD_ASESOR_RECIBIDAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_NOVEDAD_ASESOR_RECIBIDAS_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_NOVEDAD_ASESOR_RECIBIDAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_NOVEDAD_ASESOR_RECIBIDAS_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<RespuestaResponseDTO>> consultarNovedadAsesorRecibidas(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var estudianteUsuario = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarRespuestasNovedadAsesorRecibidasInteractor.ejecutar(
                ConsultarRespuestasNovedadAsesorRecibidasRequestMapper.toQuery(
                        request, estudianteUsuario));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(RespuestaResponseMapper::toResponse)));
    }
}
