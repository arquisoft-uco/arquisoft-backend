package com.arquisoft.proyectos.application.coordinador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionCoordinadorResult {

    record Removida(UUID coordinador) implements RemocionCoordinadorResult {}

    record Lapida(UUID coordinador) implements RemocionCoordinadorResult {}

    record Descartada(UUID coordinador, Instant ocurridoEnVigente) implements RemocionCoordinadorResult {}
}
