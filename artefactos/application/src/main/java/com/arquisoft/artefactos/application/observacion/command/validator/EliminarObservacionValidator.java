package com.arquisoft.artefactos.application.observacion.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.EliminacionObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface EliminarObservacionValidator {

    void validar(EliminacionObservacionDomain eliminacion, ObservacionDomain observacion,
                 RevisionAsesorDomain revision, ArtefactoDomain artefacto);
}
