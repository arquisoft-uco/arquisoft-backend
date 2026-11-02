package com.arquisoft.artefactos.application.revisionasesor.command.validator;

import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.PrecondicionesCambioEstadoRevisionAsesor;

public interface CambiarEstadoRevisionAsesorValidator {

    void validar(CambioEstadoRevisionAsesorDomain cambio, PrecondicionesCambioEstadoRevisionAsesor precondiciones);
}
