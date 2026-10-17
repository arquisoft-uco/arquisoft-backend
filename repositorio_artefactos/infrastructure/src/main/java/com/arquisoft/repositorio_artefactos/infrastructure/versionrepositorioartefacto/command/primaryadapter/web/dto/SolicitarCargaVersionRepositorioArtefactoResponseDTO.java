package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SolicitarCargaVersionRepositorioArtefactoResponseDTO(
        UUID carga,
        String url,
        Map<String, String> campos,
        Instant expiraEn) {
}
