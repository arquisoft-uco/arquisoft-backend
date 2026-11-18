package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.dto;

import java.util.UUID;

public record PlanRealProyectoResponseDTO(
        UUID id,
        UUID tarea,
        String descripcionTarea,
        UUID mapaRuta,
        int tiempoRealDias,
        String descripcionReal
) {
}
