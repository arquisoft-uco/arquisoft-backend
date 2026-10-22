package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CargaVersionRepositorioArtefactoResult(
        UUID carga,
        String url,
        Map<String, String> campos,
        Instant expiraEn
) {
}
