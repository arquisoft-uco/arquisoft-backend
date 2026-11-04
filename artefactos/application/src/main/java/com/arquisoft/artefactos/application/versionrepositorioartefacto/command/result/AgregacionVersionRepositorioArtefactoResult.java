package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result;

import java.util.UUID;

public sealed interface AgregacionVersionRepositorioArtefactoResult {

    record Agregada(UUID versionRepositorioArtefacto) implements AgregacionVersionRepositorioArtefactoResult {}

    record Duplicada(UUID versionRepositorioArtefacto) implements AgregacionVersionRepositorioArtefactoResult {}
}
