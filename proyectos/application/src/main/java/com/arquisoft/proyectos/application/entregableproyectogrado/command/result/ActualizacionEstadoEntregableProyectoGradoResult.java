package com.arquisoft.proyectos.application.entregableproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionEstadoEntregableProyectoGradoResult {

    record Actualizado(UUID entregableProyectoGrado, String estadoEntregable)
            implements ActualizacionEstadoEntregableProyectoGradoResult {}

    record Descartado(UUID entregableProyectoGrado, Instant ocurridoEnVigente)
            implements ActualizacionEstadoEntregableProyectoGradoResult {}
}
