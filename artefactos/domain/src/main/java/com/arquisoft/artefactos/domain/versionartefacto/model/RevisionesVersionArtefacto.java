package com.arquisoft.artefactos.domain.versionartefacto.model;

import java.util.UUID;

public record RevisionesVersionArtefacto(UUID versionArtefacto, int numeroVersion, boolean tieneRevision) {}
