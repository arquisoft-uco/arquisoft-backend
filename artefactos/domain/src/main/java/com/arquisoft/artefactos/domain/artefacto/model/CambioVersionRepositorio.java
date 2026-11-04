package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public record CambioVersionRepositorio(
        VersionRepositorioArtefactoDomain actual, VersionRepositorioArtefactoDomain elegida) {}
