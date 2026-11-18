package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public record PesoArtefactoReactivable(
        ArtefactoDomain artefacto,
        VersionRepositorioArtefactoDomain versionRepositorio,
        long pesoVigenteBytes) {}
