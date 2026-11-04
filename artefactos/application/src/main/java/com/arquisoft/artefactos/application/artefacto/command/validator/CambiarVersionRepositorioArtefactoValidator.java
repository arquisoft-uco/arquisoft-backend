package com.arquisoft.artefactos.application.artefacto.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public interface CambiarVersionRepositorioArtefactoValidator {

    void validar(
            CambioVersionRepositorioArtefactoDomain cambio,
            ArtefactoDomain artefacto,
            AsesorDomain asesor,
            VersionRepositorioArtefactoDomain actual,
            VersionRepositorioArtefactoDomain elegida,
            RevisionVersionAnterior versionAnterior);
}
