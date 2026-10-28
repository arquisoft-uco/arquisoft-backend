package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity;

import java.util.UUID;

public record EvaluacionCuantitativaAsesorEntity(UUID id, UUID evaluacionAsesor, UUID item, Integer puntaje) {
}
