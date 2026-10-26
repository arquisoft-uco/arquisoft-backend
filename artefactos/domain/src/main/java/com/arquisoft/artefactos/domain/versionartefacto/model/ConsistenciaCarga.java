package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record ConsistenciaCarga(UUID carga, String etagConsultado, String etagAnalizado) {}
