package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.RegistrarUsuarioRequestDTO;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.RegistrarUsuarioResponseDTO;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.mapper.RegistrarUsuarioRequestMapper;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class RegistrarUsuarioController {

    private final RegistrarUsuarioInteractor registrarUsuarioInteractor;

    @PostMapping
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_USUARIO_CREATE)
    @Operation(
            summary = UsuariosApiMessages.Usuario.REGISTRAR_SUMMARY,
            description = UsuariosApiMessages.Usuario.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = UsuariosApiMessages.Usuario.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.Usuario.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = UsuariosApiMessages.Usuario.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = UsuariosApiMessages.Usuario.REGISTRAR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarUsuarioResponseDTO> registrar(
            @RequestBody RegistrarUsuarioRequestDTO request) {

        UUID id = registrarUsuarioInteractor.ejecutar(RegistrarUsuarioRequestMapper.toCommand(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarUsuarioResponseDTO(id));
    }
}
