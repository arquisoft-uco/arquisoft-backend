package com.arquisoft.evaluaciones.application.jurado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionJuradoResult {

    record Agregada(UUID jurado) implements AgregacionJuradoResult {}

    record Reactivada(UUID jurado) implements AgregacionJuradoResult {}

    record Duplicada(UUID jurado) implements AgregacionJuradoResult {}

    record Descartada(UUID jurado, Instant ocurridoEnVigente) implements AgregacionJuradoResult {}
}
