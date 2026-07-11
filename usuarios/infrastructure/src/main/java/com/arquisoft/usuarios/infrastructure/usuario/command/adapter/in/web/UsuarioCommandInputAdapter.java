package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web;

import com.arquisoft.usuarios.application.usuario.command.port.in.CrearUsuarioInputPort;
import com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto.CrearUsuarioRequestDTO;
import com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto.CrearUsuarioResponseDTO;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestion de usuarios del sistema")
public class UsuarioCommandInputAdapter {

    private final CrearUsuarioInputPort crearUsuarioInputPort;

    @PostMapping
    @PreAuthorize("hasAuthority('usuarios:usuario:create')")
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario en el sistema con el email y rol indicados. "
                    + "Publica el evento UsuarioCreadoEvent para que otros contextos registren "
                    + "al usuario en sus bases de datos espejo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = CrearUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (email mal formado, rol nulo)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos — se requiere rol administrador")
    })
    public ResponseEntity<CrearUsuarioResponseDTO> crear(
            @Valid @RequestBody CrearUsuarioRequestDTO request) {

        UUID id = crearUsuarioInputPort.ejecutar(request.toCommand());

        CrearUsuarioResponseDTO response = CrearUsuarioResponseDTO.builder()
                .id(id)
                .email(request.email())
                .rol(request.rol().toDomain().getCode())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
