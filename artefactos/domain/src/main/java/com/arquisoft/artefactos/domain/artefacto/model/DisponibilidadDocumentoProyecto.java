package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;

public record DisponibilidadDocumentoProyecto(
        VersionRepositorioArtefactoDomain version, List<VersionRepositorioArtefactoDomain> versionesAsignadas) {}
