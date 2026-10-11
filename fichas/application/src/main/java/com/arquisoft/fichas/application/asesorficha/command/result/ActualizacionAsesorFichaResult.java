package com.arquisoft.fichas.application.asesorficha.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionAsesorFichaResult {

    record Actualizada(UUID asesorFicha) implements ActualizacionAsesorFichaResult {}

    record Descartada(UUID asesorFicha, Instant ocurridoEnVigente) implements ActualizacionAsesorFichaResult {}

    record NoReplicado(UUID asesorFicha) implements ActualizacionAsesorFichaResult {}
}
