package com.arquisoft.artefactos.application.versionartefacto.query.criteria;

import java.util.UUID;

public record VersionArtefactoDetalleCriteria(
        UUID versionArtefacto,
        UUID actor,
        boolean comoAsesor,
        boolean comoEstudiante
) {}
