package com.arquisoft.proyectos.application.asesor.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionAsesorResult {

    record Actualizada(UUID asesor) implements ActualizacionAsesorResult {}

    record Descartada(UUID asesor, Instant ocurridoEnVigente) implements ActualizacionAsesorResult {}

    record NoReplicado(UUID asesor) implements ActualizacionAsesorResult {}
}
