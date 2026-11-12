package com.arquisoft.entregables.application.artefacto.command.result;

import java.util.UUID;

public sealed interface OmisionArtefactoResult {

    record Omitida(UUID artefacto) implements OmisionArtefactoResult {}

    record SinVersionCerrada(UUID artefacto) implements OmisionArtefactoResult {}

    record Descartada(UUID artefacto) implements OmisionArtefactoResult {}
}
