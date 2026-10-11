package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity;

import java.util.UUID;

public record ObservacionEvaluacionEntity(UUID id, UUID evaluacionFichaPerfil, String observacion) {
}
