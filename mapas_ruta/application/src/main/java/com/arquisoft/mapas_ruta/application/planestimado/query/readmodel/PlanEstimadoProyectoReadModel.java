package com.arquisoft.mapas_ruta.application.planestimado.query.readmodel;

import java.util.UUID;

public record PlanEstimadoProyectoReadModel(
        UUID id,
        UUID tarea,
        String descripcionTarea,
        UUID mapaRuta,
        int tiempoEstimadoDias,
        String descripcionEstimado
) {
}
