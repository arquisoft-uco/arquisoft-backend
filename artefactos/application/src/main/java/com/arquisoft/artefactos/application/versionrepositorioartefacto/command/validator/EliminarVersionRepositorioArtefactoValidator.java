package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface EliminarVersionRepositorioArtefactoValidator {

    void validar(
            EliminacionVersionRepositorioArtefactoDomain eliminacion,
            VersionRepositorioArtefactoDomain version,
            boolean enUso);
}
