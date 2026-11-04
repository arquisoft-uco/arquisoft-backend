package com.arquisoft.mapas_ruta.application.planestimado.query.readmodel;

import java.util.UUID;

public record PlanEstimadoReadModel(
        UUID id,
        UUID tarea,
        int tiempoEstimadoDias,
        String descripcionEstimado
) {
}
