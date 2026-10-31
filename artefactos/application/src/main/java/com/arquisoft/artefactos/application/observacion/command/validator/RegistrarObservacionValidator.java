package com.arquisoft.artefactos.application.observacion.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface RegistrarObservacionValidator {

    void validar(RegistroObservacionDomain registro, RevisionAsesorDomain revision, ArtefactoDomain artefacto,
                 boolean descripcionExiste);
}
