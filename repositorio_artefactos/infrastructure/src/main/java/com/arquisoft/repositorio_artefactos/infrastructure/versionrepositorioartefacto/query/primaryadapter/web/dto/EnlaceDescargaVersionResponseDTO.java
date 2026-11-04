package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto;

import java.time.Instant;

public record EnlaceDescargaVersionResponseDTO(String url, Instant expiraEn) {
}
