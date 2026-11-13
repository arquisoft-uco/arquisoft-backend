package com.arquisoft.biblioteca.application.autor.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionAutorResult {

    record Agregada(UUID autor) implements AgregacionAutorResult {}

    record Reactivada(UUID autor) implements AgregacionAutorResult {}

    record Duplicada(UUID autor) implements AgregacionAutorResult {}

    record Descartada(UUID autor, Instant ocurridoEnVigente) implements AgregacionAutorResult {}
}
