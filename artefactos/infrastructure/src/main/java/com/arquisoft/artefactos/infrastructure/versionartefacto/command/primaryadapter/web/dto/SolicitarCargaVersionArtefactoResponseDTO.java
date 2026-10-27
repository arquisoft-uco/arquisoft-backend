package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SolicitarCargaVersionArtefactoResponseDTO(
        UUID carga,
        String url,
        Map<String, String> campos,
        Instant expiraEn) {
}
