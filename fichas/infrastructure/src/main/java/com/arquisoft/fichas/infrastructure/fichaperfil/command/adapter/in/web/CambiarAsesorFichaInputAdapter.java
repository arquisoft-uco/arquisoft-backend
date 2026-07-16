package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaInputPort;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.CambiarAsesorFichaRequestDTO;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class CambiarAsesorFichaInputAdapter {

    private final CambiarAsesorFichaInputPort cambiarAsesorFichaInputPort;

    @PatchMapping("/{id}/asesor-ficha")
    @Operation(
            summary = "Cambiar asesor de ficha perfil",
            description = "Permite al Coordinador cambiar el asesor asignado a una ficha de perfil existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asesor cambiado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Ficha o asesor no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para realizar esta acción"),
            @ApiResponse(responseCode = "422", description = "Invariante violada (mismo asesor o estado terminal)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('fichas:ficha-perfil:update-asesor')")
    public ResponseEntity<Void> cambiarAsesor(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarAsesorFichaRequestDTO request
    ) {
        cambiarAsesorFichaInputPort.ejecutar(request.toCommand(id));
        return ResponseEntity.noContent().build();
    }
}
