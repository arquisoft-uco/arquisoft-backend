package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;

import java.util.List;
import java.util.UUID;

public interface VersionRepositorioArtefactoQueryOutputPort {

    List<VersionRepositorioArtefactoReadModel> consultarPorRepositorio(UUID repositorioArtefacto);
}
