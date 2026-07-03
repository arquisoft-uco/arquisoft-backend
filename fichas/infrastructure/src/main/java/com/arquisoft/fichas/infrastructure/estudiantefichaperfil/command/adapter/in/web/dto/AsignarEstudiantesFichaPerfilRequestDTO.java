package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilRequestDTO(
        @NotEmpty(message = "La lista de estudiantes es obligatoria y no puede estar vacía")
        @Size(max = 3, message = "No se pueden asignar más de 3 estudiantes")
        List<UUID> estudiantesIds
) {
    public AsignarEstudiantesFichaPerfilCommand toCommand(UUID fichaPerfilId) {
        return new AsignarEstudiantesFichaPerfilCommand(fichaPerfilId, estudiantesIds);
    }
}
