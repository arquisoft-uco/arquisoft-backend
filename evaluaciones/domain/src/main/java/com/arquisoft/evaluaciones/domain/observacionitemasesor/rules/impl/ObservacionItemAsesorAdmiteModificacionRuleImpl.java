package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ModificacionObservacionItemAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.EstadoModificacionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorAdmiteModificacionRule;

public class ObservacionItemAsesorAdmiteModificacionRuleImpl implements ObservacionItemAsesorAdmiteModificacionRule {

    @Override
    public void validar(EstadoModificacionObservacionItemAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new ModificacionObservacionItemAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
