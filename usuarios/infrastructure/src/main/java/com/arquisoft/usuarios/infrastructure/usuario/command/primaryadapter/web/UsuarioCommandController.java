package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.CrearUsuarioInteractor;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class UsuarioCommandController {

    private final CrearUsuarioInteractor crearUsuarioInteractor;

    @PostMapping
    @PreAuthorize("hasAuthority('usuarios:usuario:create')")
    @Operation(
            summary = UsuariosApiMessages.Usuario.CREAR_SUMMARY,
            description = UsuariosApiMessages.Usuario.CREAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = UsuariosApiMessages.Usuario.CREAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = CrearUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = UsuariosApiMessages.Usuario.CREAR_RESP_400),
            @ApiResponse(responseCode = "401", description = UsuariosApiMessages.Usuario.CREAR_RESP_401),
            @ApiResponse(responseCode = "403", description = UsuariosApiMessages.Usuario.CREAR_RESP_403)
    })
    public ResponseEntity<CrearUsuarioResponseDTO> crear(
            @Valid @RequestBody CrearUsuarioRequestDTO request) {

        UUID id = crearUsuarioInteractor.ejecutar(request.toCommand());

        var response = CrearUsuarioResponseDTO.builder()
                .id(id)
                .email(request.email())
                .rol(request.rol().toDomain().getCodigo())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
