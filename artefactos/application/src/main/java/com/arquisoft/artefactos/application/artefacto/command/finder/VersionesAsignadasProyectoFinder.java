package com.arquisoft.artefactos.application.artefacto.command.finder;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface VersionesAsignadasProyectoFinder extends Finder<UUID, List<VersionRepositorioArtefactoDomain>> {
}
