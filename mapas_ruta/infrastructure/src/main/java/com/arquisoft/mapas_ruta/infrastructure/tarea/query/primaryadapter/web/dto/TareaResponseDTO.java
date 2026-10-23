package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TareaResponseDTO(
        UUID id,
        String descripcion,
        UUID mapaRuta,
        LocalDate fechaInicioMapaRuta,
        LocalDate fechaFinMapaRuta,
        String estadoItem,
        String nombreEstadoItem,
        UUID asesor,
        String nombreAsesor,
        String emailAsesor
) {
}
