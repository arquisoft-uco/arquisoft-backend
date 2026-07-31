package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaRequestDTO(

        @NotBlank(message = FichasMessages.EstadoEvaluacionFicha.EVALUACION_OBLIGATORIA_MSG)
        @UuidValido(message = FichasMessages.EstadoEvaluacionFicha.EVALUACION_FORMATO_UUID_MSG)
        String evaluacionFichaPerfil,

        @NotBlank(message = FichasMessages.EstadoEvaluacionFicha.ESTADO_OBLIGATORIO_MSG)
        @Size(max = FichasMessages.EstadoEvaluacionFicha.ESTADO_MAX,
                message = FichasMessages.EstadoEvaluacionFicha.ESTADO_MAX_MSG)
        String estadoEvaluacion) {

    public AgregarEstadoEvaluacionFichaCommand toCommand(UUID representanteComite) {
        return new AgregarEstadoEvaluacionFichaCommand(
                UtilUUID.generateUUIDFromString(evaluacionFichaPerfil),
                estadoEvaluacion,
                representanteComite);
    }
}
