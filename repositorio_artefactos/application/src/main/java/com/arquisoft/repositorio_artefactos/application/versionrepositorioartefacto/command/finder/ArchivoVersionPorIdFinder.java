package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface ArchivoVersionPorIdFinder extends Finder<UUID, ArchivoVersionDomain> {
}
