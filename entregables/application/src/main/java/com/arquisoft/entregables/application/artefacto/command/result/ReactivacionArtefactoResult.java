package com.arquisoft.entregables.application.artefacto.command.result;

import java.util.UUID;

public sealed interface ReactivacionArtefactoResult {

    record Reactivada(UUID artefacto) implements ReactivacionArtefactoResult {}

    record Anticipada(UUID artefacto) implements ReactivacionArtefactoResult {}

    record Descartada(UUID artefacto) implements ReactivacionArtefactoResult {}
}
