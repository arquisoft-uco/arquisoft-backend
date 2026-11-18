package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionEstadoProyectoGradoResult {

    record Actualizado(UUID proyectoGrado, String estadoNuevo) implements ActualizacionEstadoProyectoGradoResult {}

    record Descartado(UUID proyectoGrado, Instant ocurridoEnGuardado)
            implements ActualizacionEstadoProyectoGradoResult {}
}
