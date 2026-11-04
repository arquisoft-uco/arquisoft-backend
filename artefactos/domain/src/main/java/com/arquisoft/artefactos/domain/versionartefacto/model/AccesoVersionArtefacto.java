package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record AccesoVersionArtefacto(
        UUID versionArtefacto,
        UUID actor,
        boolean comoAsesor,
        boolean comoEstudiante,
        UUID asesor,
        boolean participa
) {}
