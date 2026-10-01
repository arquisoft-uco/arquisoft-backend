package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.AutenticarUsuarioInteractor;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.IniciarSesionRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.IniciarSesionResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.IniciarSesionRequestMapper;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.IniciarSesionResponseMapper;
import com.arquisoft.shared.message.annotation.SeguridadApiMessages;
import com.arquisoft.shared.message.annotation.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.seguridad.auth.base:/auth}")
@RequiredArgsConstructor
@Tag(name = SeguridadApiMessages.Autenticacion.TAG_NAME,
        description = SeguridadApiMessages.Autenticacion.TAG_DESCRIPTION)
public class IniciarSesionController {

    private final AutenticarUsuarioInteractor autenticarUsuarioInteractor;

    @Deprecated(since = SeguridadApiMessages.Autenticacion.INICIAR_SESION_DEPRECADO_DESDE)
    @PostMapping("${rutas.seguridad.auth.login:/login}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.INICIAR_SESION_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_DESCRIPTION,
            deprecated = true
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IniciarSesionResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_400,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SeguridadApiMessages.Autenticacion.INICIAR_SESION_RESP_401,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<IniciarSesionResponseDTO> ejecutar(
            @RequestBody IniciarSesionRequestDTO solicitud) {

        var resultado = autenticarUsuarioInteractor.ejecutar(IniciarSesionRequestMapper.toCommand(solicitud));

        return ResponseEntity.ok(IniciarSesionResponseMapper.toResponse(resultado));
    }
}
