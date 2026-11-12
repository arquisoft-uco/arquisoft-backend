package com.arquisoft.entregables.application.artefacto.command.finder;

import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ArtefactosVigentesProyectoFinder extends Finder<UUID, List<ArtefactoDomain>> {
}
