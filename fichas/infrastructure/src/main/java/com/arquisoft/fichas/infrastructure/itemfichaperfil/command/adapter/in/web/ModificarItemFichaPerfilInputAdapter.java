package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.ModificarItemFichaPerfilRequestDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class ModificarItemFichaPerfilInputAdapter {

    private final ModificarItemFichaPerfilInputPort modificarItemFichaPerfilInputPort;

    @PatchMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('fichas:item-ficha-perfil:update')")
    @Operation(
            summary = "Modificar contenido de ítem",
            description = "Permite a un estudiante modificar el contenido de un ítem de su propia ficha de perfil",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Ítem modificado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ítem no encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso o estudiante no propietario de la ficha"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Datos inválidos o ficha en estado no modificable — fieldErrors"
            )
    })
    public ResponseEntity<Void> modificarItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody ModificarItemFichaPerfilRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID estudianteId = UUID.fromString(jwt.getSubject());

        modificarItemFichaPerfilInputPort.ejecutar(dto.toCommand(itemId, estudianteId));

        return ResponseEntity.noContent().build();
    }
}
