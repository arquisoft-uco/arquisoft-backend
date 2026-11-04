package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto;

public record SolicitarCargaVersionRepositorioArtefactoRequestDTO(
        String nombreArchivo,
        String tipoContenido,
        Long tamanoBytes) {
}
