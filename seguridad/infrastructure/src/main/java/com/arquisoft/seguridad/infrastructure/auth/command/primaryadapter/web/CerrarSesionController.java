package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.CerrarSesionInteractor;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.CerrarSesionResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.CerrarSesionRequestMapper;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.SeguridadApiMessages;
import com.arquisoft.shared.message.annotation.ApiCodes;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.seguridad.auth.base:/auth}")
@RequiredArgsConstructor
@Tag(name = SeguridadApiMessages.Autenticacion.TAG_NAME,
        description = SeguridadApiMessages.Autenticacion.TAG_DESCRIPTION)
public class CerrarSesionController {

    private final CerrarSesionInteractor cerrarSesionInteractor;

    @PostMapping("${rutas.seguridad.auth.logout:/logout}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.CERRAR_SESION_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CerrarSesionResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SeguridadApiMessages.Autenticacion.CERRAR_SESION_RESP_401)
    })
    public ResponseEntity<CerrarSesionResponseDTO> ejecutar(@AuthenticationPrincipal Jwt jwt) {
        cerrarSesionInteractor.ejecutar(CerrarSesionRequestMapper.toCommand(jwt));
        return ResponseEntity.ok(CerrarSesionResponseDTO.builder().build());
    }
}
