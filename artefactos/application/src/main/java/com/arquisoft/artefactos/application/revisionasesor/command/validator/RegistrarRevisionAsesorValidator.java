package com.arquisoft.artefactos.application.revisionasesor.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

public interface RegistrarRevisionAsesorValidator {

    void validar(RegistroRevisionAsesorDomain registro, VersionArtefactoDomain version, ArtefactoDomain artefacto,
                 boolean esUltima, boolean existeAbierta);
}
