package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto;

public record SubirVersionRepositorioArtefactoRequestDTO(
        String carga,
        String nombreDocumento,
        Boolean nuevoDocumento) {
}
