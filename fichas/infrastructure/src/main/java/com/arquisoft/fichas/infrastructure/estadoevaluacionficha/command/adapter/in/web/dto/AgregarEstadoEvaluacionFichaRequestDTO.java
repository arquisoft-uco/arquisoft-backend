package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaRequestDTO(
        @NotNull(message = "El ID de la evaluación es obligatorio")
        UUID evaluacionFichaPerfilId,

        @NotBlank(message = "El ID del estado es obligatorio")
        @Size(max = 50, message = "El ID del estado no puede exceder 50 caracteres")
        String estadoEvaluacionId) {

    public AgregarEstadoEvaluacionFichaCommand toCommand(UUID representanteComiteId) {
        return new AgregarEstadoEvaluacionFichaCommand(
                evaluacionFichaPerfilId, estadoEvaluacionId, representanteComiteId);
    }
}
