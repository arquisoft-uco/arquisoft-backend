package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface GenerarRepositorioArtefactoUseCase extends UseCase<RepositorioArtefactoDomain, UUID> {
}
