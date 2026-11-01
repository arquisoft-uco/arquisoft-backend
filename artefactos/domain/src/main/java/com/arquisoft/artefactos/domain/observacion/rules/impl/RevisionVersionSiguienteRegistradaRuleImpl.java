package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguienteRegistradaRule;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteNoRegistradaException;

public class RevisionVersionSiguienteRegistradaRuleImpl implements RevisionVersionSiguienteRegistradaRule {

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (entrada.esReapertura()
                && entrada.revisionVersionSiguiente().esVacio()) {
            throw new RevisionVersionSiguienteNoRegistradaException(
                    entrada.observacion(), entrada.numeroVersion() + 1);
        }
    }
}
