package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.ModificarFichaPerfilRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class ModificarFichaPerfilInputAdapter {

    private final ModificarFichaPerfilInputPort modificarFichaPerfilInputPort;

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('fichas:ficha-perfil:update')")
    @Operation(
            summary = "Modificar ficha de perfil",
            description = "Permite a un estudiante modificar el título de su propia ficha de perfil.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ficha modificada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Título duplicado, ficha no encontrada o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin la autoridad fichas:ficha-perfil:update, o no es propietario de la ficha")
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID id,
            @Valid @RequestBody ModificarFichaPerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID estudianteId = UUID.fromString(jwt.getSubject());
        modificarFichaPerfilInputPort.ejecutar(request.toCommand(id, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
