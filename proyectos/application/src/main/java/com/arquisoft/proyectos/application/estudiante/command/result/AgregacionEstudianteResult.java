package com.arquisoft.proyectos.application.estudiante.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionEstudianteResult {

    record Agregada(UUID estudiante) implements AgregacionEstudianteResult {}

    record Duplicada(UUID estudiante) implements AgregacionEstudianteResult {}

    record Descartada(UUID estudiante, Instant ocurridoEnVigente) implements AgregacionEstudianteResult {}
}
