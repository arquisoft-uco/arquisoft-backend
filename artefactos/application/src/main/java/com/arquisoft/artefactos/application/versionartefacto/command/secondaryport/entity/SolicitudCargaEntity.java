package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity;

import java.util.UUID;

public record SolicitudCargaEntity(
        UUID carga,
        UUID artefacto,
        UUID solicitante,
        String nombreArchivo,
        String tipoContenido,
        long tamanoBytes
) {
}
