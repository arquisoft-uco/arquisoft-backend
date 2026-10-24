package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface VersionesRepositorioArtefactoPorIdsFinder
        extends Finder<List<UUID>, List<VersionRepositorioArtefactoDomain>> {
}
