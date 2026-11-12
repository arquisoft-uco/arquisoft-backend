package com.arquisoft.entregables.application.artefacto.command.result;

import java.util.UUID;

public sealed interface ReactivacionArtefactoResult {

    record Reactivada(UUID artefacto) implements ReactivacionArtefactoResult {}

    record SinVersionCerrada(UUID artefacto) implements ReactivacionArtefactoResult {}

    record Descartada(UUID artefacto) implements ReactivacionArtefactoResult {}
}
