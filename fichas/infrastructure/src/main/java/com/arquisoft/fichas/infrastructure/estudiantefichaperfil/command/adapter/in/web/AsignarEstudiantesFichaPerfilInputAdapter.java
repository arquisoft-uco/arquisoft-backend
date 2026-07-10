package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto.AsignarEstudiantesFichaPerfilRequestDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class AsignarEstudiantesFichaPerfilInputAdapter {

    private final AsignarEstudiantesFichaPerfilInputPort asignarEstudiantesFichaPerfilInputPort;

    @PostMapping("/{fichaPerfilId}/estudiantes")
    @PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:create')")
    @Operation(
            summary = "Asignar estudiantes a ficha de perfil existente",
            description = "Permite al coordinador asignar entre 1 y 3 estudiantes a una ficha de perfil ya existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Estudiantes asignados exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ficha no encontrada, estudiante no encontrado, duplicado en lista o ya asignado"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Límite de estudiantes excedido"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permiso para asignar estudiantes"
            )
    })
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable UUID fichaPerfilId,
            @Valid @RequestBody AsignarEstudiantesFichaPerfilRequestDTO dto) {

        asignarEstudiantesFichaPerfilInputPort.ejecutar(dto.toCommand(fichaPerfilId));

        return ResponseEntity.noContent().build();
    }
}
