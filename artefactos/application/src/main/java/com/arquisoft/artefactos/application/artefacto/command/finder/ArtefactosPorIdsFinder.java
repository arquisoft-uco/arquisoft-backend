package com.arquisoft.artefactos.application.artefacto.command.finder;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ArtefactosPorIdsFinder extends Finder<List<UUID>, List<ArtefactoDomain>> {
}
