package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity;

import java.util.UUID;

public record EvaluacionEntity(UUID id, UUID entregable, String estado) {
}
