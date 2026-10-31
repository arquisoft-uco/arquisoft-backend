package com.arquisoft.artefactos.application.observacion.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface EliminarObservacionValidator {

    void validar(EliminacionObservacionDomain eliminacion, RevisionAsesorDomain revision, ArtefactoDomain artefacto);
}
