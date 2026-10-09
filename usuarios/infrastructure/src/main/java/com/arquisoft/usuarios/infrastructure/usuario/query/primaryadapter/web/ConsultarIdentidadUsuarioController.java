package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarIdentidadUsuarioInteractor;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.IdentidadUsuarioResponseDTO;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper.ConsultarIdentidadUsuarioRequestMapper;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper.IdentidadUsuarioResponseMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class ConsultarIdentidadUsuarioController {

    private final ConsultarIdentidadUsuarioInteractor consultarIdentidadUsuarioInteractor;

    @GetMapping("${rutas.usuarios.usuarios.identidad:/{usuarioId}}")
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_USUARIO_IDENTIDAD_VIEW)
    @Operation(
            summary = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_SUMMARY,
            description = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = IdentidadUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_IDENTIDAD_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<IdentidadUsuarioResponseDTO> consultar(@PathVariable UUID usuarioId) {
        var identidad = consultarIdentidadUsuarioInteractor.ejecutar(
                ConsultarIdentidadUsuarioRequestMapper.toQuery(usuarioId));

        return ResponseEntity.ok(IdentidadUsuarioResponseMapper.toResponse(identidad));
    }
}
