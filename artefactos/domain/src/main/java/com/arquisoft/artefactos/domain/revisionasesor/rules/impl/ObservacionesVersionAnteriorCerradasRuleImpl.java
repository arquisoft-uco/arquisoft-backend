package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.ObservacionesVersionAnteriorSinCerrarException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.ObservacionesVersionAnteriorCerradasRule;

public class ObservacionesVersionAnteriorCerradasRuleImpl implements ObservacionesVersionAnteriorCerradasRule {

    @Override
    public void validar(ObservacionesVersionAnterior entrada) {
        if (entrada.existenSinCerrar()) {
            throw new ObservacionesVersionAnteriorSinCerrarException(entrada.revisionAsesor());
        }
    }
}
