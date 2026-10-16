package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import java.util.UUID;

public record ConsistenciaCarga(UUID carga, String etagConsultado, String etagAnalizado) {}
