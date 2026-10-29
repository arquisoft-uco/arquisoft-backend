package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

import java.util.UUID;

public record ExistenciaVersionArtefacto(UUID versionArtefacto, VersionArtefactoDomain version) {}
