package com.arquisoft.fichas.application.estudiante.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionEstudianteResult {

    record Actualizada(UUID estudiante) implements ActualizacionEstudianteResult {}

    record Descartada(UUID estudiante, Instant ocurridoEnVigente) implements ActualizacionEstudianteResult {}

    record NoReplicado(UUID estudiante) implements ActualizacionEstudianteResult {}
}
