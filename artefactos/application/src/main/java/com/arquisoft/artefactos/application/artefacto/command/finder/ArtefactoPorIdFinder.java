package com.arquisoft.artefactos.application.artefacto.command.finder;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface ArtefactoPorIdFinder extends Finder<UUID, ArtefactoDomain> {
}
