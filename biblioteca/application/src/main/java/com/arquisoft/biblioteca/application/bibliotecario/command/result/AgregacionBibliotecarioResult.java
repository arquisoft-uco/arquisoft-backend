package com.arquisoft.biblioteca.application.bibliotecario.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionBibliotecarioResult {

    record Agregada(UUID bibliotecario) implements AgregacionBibliotecarioResult {}

    record Reactivada(UUID bibliotecario) implements AgregacionBibliotecarioResult {}

    record Duplicada(UUID bibliotecario) implements AgregacionBibliotecarioResult {}

    record Descartada(UUID bibliotecario, Instant ocurridoEnVigente) implements AgregacionBibliotecarioResult {}
}
