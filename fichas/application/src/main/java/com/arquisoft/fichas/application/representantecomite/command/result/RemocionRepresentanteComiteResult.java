package com.arquisoft.fichas.application.representantecomite.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionRepresentanteComiteResult {

    record Removida(UUID representanteComite) implements RemocionRepresentanteComiteResult {}

    record Lapida(UUID representanteComite) implements RemocionRepresentanteComiteResult {}

    record Descartada(UUID representanteComite, Instant ocurridoEnVigente) implements RemocionRepresentanteComiteResult {}
}
