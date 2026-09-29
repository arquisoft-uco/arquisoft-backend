package com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.usuarios.application.estadousuario.query.primaryport.interactor.ConsultarEstadosUsuarioInteractor;
import com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web.dto.EstadoUsuarioResponseDTO;
import com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web.mapper.EstadoUsuarioResponseMapper;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class ConsultarEstadosUsuarioController {

    private final ConsultarEstadosUsuarioInteractor consultarEstadosUsuarioInteractor;

    @GetMapping("${rutas.usuarios.usuarios.estados:/estados}")
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_ESTADO_USUARIO_VIEW)
    @Operation(
            summary = UsuariosApiMessages.EstadoUsuario.CONSULTAR_SUMMARY,
            description = UsuariosApiMessages.EstadoUsuario.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = UsuariosApiMessages.EstadoUsuario.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EstadoUsuarioResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<EstadoUsuarioResponseDTO>> consultar() {
        var estados = consultarEstadosUsuarioInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoUsuarioResponseMapper::toResponse)
                .toList());
    }
}
