package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.dto.RespuestaResponseDTO;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper.ConsultarRespuestasRegistroModificacionUsuariosEnviadasRequestMapper;
import com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper.RespuestaResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SolicitudesApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.solicitudes.respuesta.base:/solicitudes}")
@RequiredArgsConstructor
@Tag(name = SolicitudesApiMessages.Respuesta.TAG_NAME,
        description = SolicitudesApiMessages.Respuesta.TAG_DESCRIPTION)
public class ConsultarRespuestasRegistroModificacionUsuariosEnviadasController {

    private final ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor
            consultarRespuestasRegistroModificacionUsuariosEnviadasInteractor;

    @PostMapping(
            "${rutas.solicitudes.respuesta.registro-modificacion-usuarios-enviadas:"
                    + "/registro-modificacion-usuarios/respuestas/enviadas}")
    @Operation(
            summary = SolicitudesApiMessages.Respuesta.CONSULTAR_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_SUMMARY,
            description = SolicitudesApiMessages.Respuesta.CONSULTAR_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SolicitudesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = SolicitudesApiMessages.Respuesta.CONSULTAR_REGISTRO_MODIFICACION_USUARIOS_ENVIADAS_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<RespuestaResponseDTO>> consultarRegistroModificacionUsuariosEnviadas(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var administradorUsuario = identidad.id();
        var resultado = consultarRespuestasRegistroModificacionUsuariosEnviadasInteractor.ejecutar(
                ConsultarRespuestasRegistroModificacionUsuariosEnviadasRequestMapper.toQuery(
                        request, administradorUsuario));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(RespuestaResponseMapper::toResponse)));
    }
}
