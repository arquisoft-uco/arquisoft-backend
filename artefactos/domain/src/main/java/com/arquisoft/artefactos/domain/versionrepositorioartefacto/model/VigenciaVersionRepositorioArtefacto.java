package com.arquisoft.artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public record VigenciaVersionRepositorioArtefacto(
        VersionRepositorioArtefactoDomain version, int ultimaVersionDocumento) {}
