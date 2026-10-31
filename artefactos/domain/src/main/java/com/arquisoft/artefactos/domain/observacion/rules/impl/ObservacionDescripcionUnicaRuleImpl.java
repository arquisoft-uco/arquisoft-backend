package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadObservacionRevision;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionDescripcionUnicaRule;

public class ObservacionDescripcionUnicaRuleImpl implements ObservacionDescripcionUnicaRule {

    @Override
    public void validar(UnicidadObservacionRevision entrada) {
        if (entrada.existe()) {
            throw new ObservacionDuplicadaException(entrada.revisionAsesor());
        }
    }
}
