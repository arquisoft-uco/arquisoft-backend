package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.model;

import java.time.Instant;
import java.util.Map;

public record FormularioCarga(String url, Map<String, String> campos, Instant expiraEn) {
}
