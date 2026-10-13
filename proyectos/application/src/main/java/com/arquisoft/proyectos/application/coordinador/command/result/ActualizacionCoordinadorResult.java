package com.arquisoft.proyectos.application.coordinador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionCoordinadorResult {

    record Actualizada(UUID coordinador) implements ActualizacionCoordinadorResult {}

    record Descartada(UUID coordinador, Instant ocurridoEnVigente) implements ActualizacionCoordinadorResult {}

    record NoReplicado(UUID coordinador) implements ActualizacionCoordinadorResult {}
}
