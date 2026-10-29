package com.arquisoft.artefactos.domain.artefacto.model;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

import java.util.List;
import java.util.UUID;

public record DisponibilidadRepositorioProyecto(
        VersionRepositorioArtefactoDomain version, List<UUID> repositoriosAsignados) {}
