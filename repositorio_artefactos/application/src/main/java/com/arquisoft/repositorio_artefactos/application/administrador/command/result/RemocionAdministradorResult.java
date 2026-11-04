package com.arquisoft.repositorio_artefactos.application.administrador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionAdministradorResult {

    record Removida(UUID administrador) implements RemocionAdministradorResult {}

    record Lapida(UUID administrador) implements RemocionAdministradorResult {}

    record Descartada(UUID administrador, Instant ocurridoEnVigente) implements RemocionAdministradorResult {}
}
