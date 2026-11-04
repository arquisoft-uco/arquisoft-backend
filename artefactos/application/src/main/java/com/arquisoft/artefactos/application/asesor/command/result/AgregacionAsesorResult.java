package com.arquisoft.artefactos.application.asesor.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionAsesorResult {

    record Agregada(UUID asesor) implements AgregacionAsesorResult {}

    record Reactivada(UUID asesor) implements AgregacionAsesorResult {}

    record Duplicada(UUID asesor) implements AgregacionAsesorResult {}

    record Descartada(UUID asesor, Instant ocurridoEnVigente) implements AgregacionAsesorResult {}
}
