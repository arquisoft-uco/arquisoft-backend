package com.arquisoft.fichas.application.asesorficha.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionAsesorFichaResult {

    record Agregada(UUID asesorFicha) implements AgregacionAsesorFichaResult {}

    record Reactivada(UUID asesorFicha) implements AgregacionAsesorFichaResult {}

    record Duplicada(UUID asesorFicha) implements AgregacionAsesorFichaResult {}

    record Descartada(UUID asesorFicha, Instant ocurridoEnVigente) implements AgregacionAsesorFichaResult {}
}
