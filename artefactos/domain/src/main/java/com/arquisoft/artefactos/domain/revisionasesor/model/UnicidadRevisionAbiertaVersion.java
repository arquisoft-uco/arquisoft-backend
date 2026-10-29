package com.arquisoft.artefactos.domain.revisionasesor.model;

import java.util.UUID;

public record UnicidadRevisionAbiertaVersion(UUID versionArtefacto, int numeroVersion, boolean existeAbierta) {}
