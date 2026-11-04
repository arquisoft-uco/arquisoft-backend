package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface RepositorioArtefactoPorIdFinder extends Finder<UUID, RepositorioArtefactoDomain> {
}
