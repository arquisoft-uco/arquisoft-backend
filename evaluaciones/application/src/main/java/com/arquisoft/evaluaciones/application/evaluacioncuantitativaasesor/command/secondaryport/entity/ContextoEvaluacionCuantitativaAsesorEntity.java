package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity;

import java.util.UUID;

public record ContextoEvaluacionCuantitativaAsesorEntity(UUID id, UUID evaluacionAsesor, UUID asesor, String estado) {
}
