package com.arquisoft.mapas_ruta.application.tarea.query.readmodel;

import java.time.LocalDate;
import java.util.UUID;

public record TareaReadModel(
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
