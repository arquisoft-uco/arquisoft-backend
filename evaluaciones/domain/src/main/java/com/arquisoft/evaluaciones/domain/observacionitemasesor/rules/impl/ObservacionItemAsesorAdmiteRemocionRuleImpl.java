package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.RemocionObservacionItemAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.EstadoRemocionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorAdmiteRemocionRule;

public class ObservacionItemAsesorAdmiteRemocionRuleImpl implements ObservacionItemAsesorAdmiteRemocionRule {

    @Override
    public void validar(EstadoRemocionObservacionItemAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new RemocionObservacionItemAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
