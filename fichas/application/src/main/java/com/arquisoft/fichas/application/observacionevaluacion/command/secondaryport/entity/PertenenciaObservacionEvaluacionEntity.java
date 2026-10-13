package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity;

import java.util.UUID;

public record PertenenciaObservacionEvaluacionEntity(
        UUID evaluacionFichaPerfilId, boolean esPropietario, String estadoEvaluacionId) {
}
