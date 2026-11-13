package com.arquisoft.biblioteca.application.autor.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionAutorResult {

    record Removida(UUID autor) implements RemocionAutorResult {}

    record Lapida(UUID autor) implements RemocionAutorResult {}

    record Descartada(UUID autor, Instant ocurridoEnVigente) implements RemocionAutorResult {}
}
