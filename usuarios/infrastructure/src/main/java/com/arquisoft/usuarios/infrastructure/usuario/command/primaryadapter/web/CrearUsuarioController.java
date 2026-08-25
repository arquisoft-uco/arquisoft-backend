package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.CrearUsuarioInteractor;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.CrearUsuarioRequestDTO;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.CrearUsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
public class CrearUsuarioController {

    private final CrearUsuarioInteractor crearUsuarioInteractor;

    @PostMapping
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_USUARIO_CREATE)
    @Operation(
            summary = UsuariosApiMessages.Usuario.CREAR_SUMMARY,
            description = UsuariosApiMessages.Usuario.CREAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = UsuariosApiMessages.Usuario.CREAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = CrearUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.Usuario.CREAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Usuario.CREAR_RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Usuario.CREAR_RESP_403)
    })
    public ResponseEntity<CrearUsuarioResponseDTO> crear(
            @Valid @RequestBody CrearUsuarioRequestDTO request) {

        UUID id = crearUsuarioInteractor.ejecutar(request.toCommand());

        var response = new CrearUsuarioResponseDTO(
                id, request.email(), request.rol().toDomain().getCodigo());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
