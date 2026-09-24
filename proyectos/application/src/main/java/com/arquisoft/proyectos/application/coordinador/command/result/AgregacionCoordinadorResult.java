package com.arquisoft.proyectos.application.coordinador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionCoordinadorResult {

    record Agregada(UUID coordinador) implements AgregacionCoordinadorResult {}

    record Reactivada(UUID coordinador) implements AgregacionCoordinadorResult {}

    record Duplicada(UUID coordinador) implements AgregacionCoordinadorResult {}

    record Descartada(UUID coordinador, Instant ocurridoEnVigente) implements AgregacionCoordinadorResult {}
}
