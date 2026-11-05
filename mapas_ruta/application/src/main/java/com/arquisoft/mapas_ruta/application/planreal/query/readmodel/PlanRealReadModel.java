package com.arquisoft.mapas_ruta.application.planreal.query.readmodel;

import java.util.UUID;

public record PlanRealReadModel(
        UUID id,
        UUID tarea,
        int tiempoRealDias,
        String descripcionReal
) {
}
