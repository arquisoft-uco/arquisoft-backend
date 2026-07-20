package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CambiarAsesorFichaRequestDTO(
        @NotNull(message = "El asesorFichaId es obligatorio")
        UUID asesorFichaId
) {
    public CambiarAsesorFichaCommand toCommand(UUID fichaPerfilId) {
        return new CambiarAsesorFichaCommand(fichaPerfilId, asesorFichaId);
    }
}
