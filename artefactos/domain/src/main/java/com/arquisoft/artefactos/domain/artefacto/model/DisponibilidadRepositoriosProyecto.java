package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public record DisponibilidadRepositoriosProyecto(
        List<VersionRepositorioArtefactoDomain> versiones, List<UUID> repositoriosAsignados) {}
