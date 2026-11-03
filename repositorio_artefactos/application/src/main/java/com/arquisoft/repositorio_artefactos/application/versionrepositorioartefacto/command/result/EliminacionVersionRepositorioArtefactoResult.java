package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result;

import java.util.UUID;

public sealed interface EliminacionVersionRepositorioArtefactoResult {

    record Eliminada(UUID version) implements EliminacionVersionRepositorioArtefactoResult {}

    record Inexistente(UUID version) implements EliminacionVersionRepositorioArtefactoResult {}
}
