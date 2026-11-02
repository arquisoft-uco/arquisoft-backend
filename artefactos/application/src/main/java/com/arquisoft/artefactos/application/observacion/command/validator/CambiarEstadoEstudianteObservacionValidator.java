package com.arquisoft.artefactos.application.observacion.command.validator;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public interface CambiarEstadoEstudianteObservacionValidator {

    void validar(CambioEstadoEstudianteObservacionDomain cambio, ObservacionDomain observacion,
                 RevisionAsesorDomain revision, ArtefactoDomain artefacto, boolean vinculado);
}
