package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto;

public record SolicitarCargaVersionArtefactoRequestDTO(
        String nombreArchivo,
        String tipoContenido,
        Long tamanoBytes) {
}
