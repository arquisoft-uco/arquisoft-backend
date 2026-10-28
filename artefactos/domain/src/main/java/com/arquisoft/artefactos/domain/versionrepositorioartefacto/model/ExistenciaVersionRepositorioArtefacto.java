package com.arquisoft.artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.UUID;

public record ExistenciaVersionRepositorioArtefacto(
        UUID versionRepositorioArtefacto, VersionRepositorioArtefactoDomain encontrada) {}
