package com.arquisoft.fichas.application.asesorficha.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionAsesorFichaResult {

    record Removida(UUID asesorFicha) implements RemocionAsesorFichaResult {}

    record Lapida(UUID asesorFicha) implements RemocionAsesorFichaResult {}

    record Descartada(UUID asesorFicha, Instant ocurridoEnVigente) implements RemocionAsesorFichaResult {}
}
