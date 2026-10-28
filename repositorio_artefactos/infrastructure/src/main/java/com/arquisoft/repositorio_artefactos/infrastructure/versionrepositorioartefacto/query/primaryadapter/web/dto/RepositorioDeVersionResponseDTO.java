package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto;

import java.util.UUID;

public record RepositorioDeVersionResponseDTO(UUID id, String nombreRepositorio, String descripcion) {
}
