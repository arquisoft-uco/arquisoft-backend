package com.arquisoft.biblioteca.application.bibliotecario.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionBibliotecarioResult {

    record Removida(UUID bibliotecario) implements RemocionBibliotecarioResult {}

    record Lapida(UUID bibliotecario) implements RemocionBibliotecarioResult {}

    record Descartada(UUID bibliotecario, Instant ocurridoEnVigente) implements RemocionBibliotecarioResult {}
}
