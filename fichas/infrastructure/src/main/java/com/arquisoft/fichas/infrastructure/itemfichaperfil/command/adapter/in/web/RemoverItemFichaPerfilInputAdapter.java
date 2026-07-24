package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.RemoverItemFichaPerfilInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas", description = "Gestión de fichas de perfil")
public class RemoverItemFichaPerfilInputAdapter {

    private final RemoverItemFichaPerfilInputPort inputPort;

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('fichas:item-ficha-perfil:delete')")
    @Operation(
            summary = "Remover un ítem de la ficha",
            description = "Elimina físicamente un ítem de la ficha de perfil del estudiante autenticado. " +
                    "Solo permite eliminar ítems sin revisiones asociadas.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ítem eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "El ítem no existe"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso o no es propietario de la ficha"),
            @ApiResponse(responseCode = "422", description = "El ítem tiene revisiones y no puede eliminarse")
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal Jwt jwt) {
        var estudianteId = UUID.fromString(jwt.getSubject());
        var command = new RemoverItemFichaPerfilCommand(itemId, estudianteId);
        inputPort.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
