package com.arquisoft.artefactos.application.versionartefacto.command.finder;

import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface VersionArtefactoPorIdFinder extends Finder<UUID, VersionArtefactoDomain> {
}
