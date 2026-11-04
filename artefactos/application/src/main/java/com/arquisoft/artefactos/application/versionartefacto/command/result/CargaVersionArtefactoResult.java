package com.arquisoft.artefactos.application.versionartefacto.command.result;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CargaVersionArtefactoResult(
        UUID carga,
        String url,
        Map<String, String> campos,
        Instant expiraEn
) {
}
