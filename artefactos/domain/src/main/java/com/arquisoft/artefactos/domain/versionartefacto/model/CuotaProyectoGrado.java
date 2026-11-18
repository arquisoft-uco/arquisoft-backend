package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record CuotaProyectoGrado(
        UUID proyectoGrado,
        String nombreProyecto,
        String nombreObjeto,
        long cuotaBytes,
        long usoBytes,
        long tamanoNuevoBytes) {}
