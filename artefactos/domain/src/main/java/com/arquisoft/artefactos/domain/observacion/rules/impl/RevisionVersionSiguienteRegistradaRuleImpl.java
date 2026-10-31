package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguienteRegistradaRule;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteNoRegistradaException;

public class RevisionVersionSiguienteRegistradaRuleImpl implements RevisionVersionSiguienteRegistradaRule {

    private static final int EN_LA_VERSION_ANTERIOR = 1;

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (entrada.esReapertura()
                && entrada.diferenciaVersiones() == EN_LA_VERSION_ANTERIOR
                && entrada.revisionVersionSiguiente().esVacio()) {
            throw new RevisionVersionSiguienteNoRegistradaException(
                    entrada.observacion(), entrada.numeroVersion() + 1);
        }
    }
}
