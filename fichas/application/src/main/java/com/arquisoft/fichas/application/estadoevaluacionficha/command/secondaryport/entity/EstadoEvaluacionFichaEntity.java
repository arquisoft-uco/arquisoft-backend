package com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstadoEvaluacionFichaEntity(
        UUID id,
        UUID evaluacionFichaPerfil,
        String estadoEvaluacion,
        Instant fechaActualizacion) {
}
