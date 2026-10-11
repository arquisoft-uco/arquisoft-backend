package com.arquisoft.fichas.application.estudiante.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionEstudianteResult {

    record Removida(UUID estudiante) implements RemocionEstudianteResult {}

    record Lapida(UUID estudiante) implements RemocionEstudianteResult {}

    record Descartada(UUID estudiante, Instant ocurridoEnVigente) implements RemocionEstudianteResult {}
}
