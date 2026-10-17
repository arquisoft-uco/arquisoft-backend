package com.arquisoft.evaluaciones.application.jurado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionJuradoResult {

    record Removida(UUID jurado) implements RemocionJuradoResult {}

    record Lapida(UUID jurado) implements RemocionJuradoResult {}

    record Descartada(UUID jurado, Instant ocurridoEnVigente) implements RemocionJuradoResult {}
}
