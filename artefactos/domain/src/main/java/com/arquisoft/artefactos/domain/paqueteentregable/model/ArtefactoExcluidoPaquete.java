package com.arquisoft.artefactos.domain.paqueteentregable.model;

import java.util.UUID;

public record ArtefactoExcluidoPaquete(
        UUID artefacto,
        String nombreDocumento,
        MotivoExclusionPaquete motivo,
        int ultimaVersion
) {}
