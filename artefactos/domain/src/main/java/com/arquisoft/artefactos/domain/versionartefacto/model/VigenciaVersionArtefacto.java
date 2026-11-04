package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record VigenciaVersionArtefacto(UUID versionArtefacto, int numeroVersion, boolean esUltima) {}
