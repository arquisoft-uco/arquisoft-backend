package com.arquisoft.artefactos.application.revisionasesor.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface EliminarRevisionAsesorValidator {

    void validar(EliminacionRevisionAsesorDomain eliminacion, RevisionAsesorDomain revision,
                 ArtefactoDomain artefacto);
}
