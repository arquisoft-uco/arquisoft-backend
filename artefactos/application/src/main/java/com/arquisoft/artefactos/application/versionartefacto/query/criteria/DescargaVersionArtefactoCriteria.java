package com.arquisoft.artefactos.application.versionartefacto.query.criteria;

import java.util.UUID;

public record DescargaVersionArtefactoCriteria(
        UUID versionArtefacto,
        UUID actor,
        boolean comoAsesor,
        boolean comoEstudiante
) {}
