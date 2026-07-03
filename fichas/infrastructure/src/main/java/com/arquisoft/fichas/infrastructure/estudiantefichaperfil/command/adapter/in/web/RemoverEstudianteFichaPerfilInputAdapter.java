package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas", description = "Gestión de fichas de perfil")
public class RemoverEstudianteFichaPerfilInputAdapter {

    private final RemoverEstudianteFichaPerfilInputPort inputPort;

    @DeleteMapping("/{fichaPerfilId}/estudiantes/{estudianteId}")
    @PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:delete')")
    @Operation(
            summary = "Remover estudiante de ficha perfil",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estudiante removido exitosamente"),
            @ApiResponse(responseCode = "400", description = "Ficha, estudiante o relación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permiso para remover estudiantes")
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID fichaPerfilId,
            @PathVariable UUID estudianteId
    ) {
        var command = new RemoverEstudianteFichaPerfilCommand(fichaPerfilId, estudianteId);
        inputPort.ejecutar(command);
        return ResponseEntity.noContent().build();
    }
}
