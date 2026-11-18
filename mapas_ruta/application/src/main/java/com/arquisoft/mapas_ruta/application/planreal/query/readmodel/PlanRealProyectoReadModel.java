package com.arquisoft.mapas_ruta.application.planreal.query.readmodel;

import java.util.UUID;

public record PlanRealProyectoReadModel(
        UUID id,
        UUID tarea,
        String descripcionTarea,
        UUID mapaRuta,
        int tiempoRealDias,
        String descripcionReal
) {
}
