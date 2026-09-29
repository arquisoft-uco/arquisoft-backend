package com.arquisoft.proyectos.application.asesor.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionAsesorResult {

    record Removida(UUID asesor) implements RemocionAsesorResult {}

    record Lapida(UUID asesor) implements RemocionAsesorResult {}

    record Descartada(UUID asesor, Instant ocurridoEnVigente) implements RemocionAsesorResult {}
}
