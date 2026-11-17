package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarMiPerfilInteractor;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.PerfilUsuarioResponseDTO;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper.ConsultarMiPerfilRequestMapper;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper.PerfilUsuarioResponseMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class ConsultarMiPerfilController {

    private final ConsultarMiPerfilInteractor consultarMiPerfilInteractor;

    @GetMapping("${rutas.usuarios.usuarios.mi-perfil:/mi/perfil}")
    @Operation(
            summary = UsuariosApiMessages.Usuario.CONSULTAR_MI_PERFIL_SUMMARY,
            description = UsuariosApiMessages.Usuario.CONSULTAR_MI_PERFIL_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_MI_PERFIL_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PerfilUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_MI_PERFIL_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = UsuariosApiMessages.Usuario.CONSULTAR_MI_PERFIL_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PerfilUsuarioResponseDTO> consultar(IdentidadActual identidad) {
        var perfil = consultarMiPerfilInteractor.ejecutar(
                ConsultarMiPerfilRequestMapper.toQuery(identidad.id()));

        return ResponseEntity.ok(PerfilUsuarioResponseMapper.toResponse(perfil));
    }
}
