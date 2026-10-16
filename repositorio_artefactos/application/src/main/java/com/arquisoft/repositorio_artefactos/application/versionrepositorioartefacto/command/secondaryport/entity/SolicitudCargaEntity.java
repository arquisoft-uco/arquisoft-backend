package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity;

import java.util.UUID;

public record SolicitudCargaEntity(
        UUID carga,
        UUID repositorio,
        UUID solicitante,
        String nombreArchivo,
        String tipoContenido,
        long tamanoBytes
) {
}
