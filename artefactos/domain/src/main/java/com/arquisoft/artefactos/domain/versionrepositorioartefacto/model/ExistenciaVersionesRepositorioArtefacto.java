package com.arquisoft.artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public record ExistenciaVersionesRepositorioArtefacto(
        List<UUID> solicitadas, List<VersionRepositorioArtefactoDomain> encontradas) {}
