package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaRequestDTO(

        @NotBlank(message = ValidationKeys.EstadoEvaluacionFicha.EVALUACION_OBLIGATORIA)
        @UuidValido(message = ValidationKeys.EstadoEvaluacionFicha.EVALUACION_UUID)
        String evaluacionFichaPerfil,

        @NotBlank(message = ValidationKeys.EstadoEvaluacionFicha.ESTADO_OBLIGATORIO)
        @Size(max = FichasLimits.EstadoEvaluacionFicha.ESTADO_MAX,
                message = ValidationKeys.EstadoEvaluacionFicha.ESTADO_MAXIMO)
        String estadoEvaluacion) {

    public AgregarEstadoEvaluacionFichaCommand toCommand(UUID representanteComite) {
        return new AgregarEstadoEvaluacionFichaCommand(
                UtilUUID.generateUUIDFromString(evaluacionFichaPerfil),
                estadoEvaluacion,
                representanteComite);
    }
}
