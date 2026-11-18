package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaAsignacionException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteCambioAsignacionRule;

public class EvaluacionAdmiteCambioAsignacionRuleImpl implements EvaluacionAdmiteCambioAsignacionRule {

    @Override
    public void validar(EstadoAsignacionEvaluacion estadoAsignacion) {
        if (estadoAsignacion.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new EvaluacionFinalizadaAsignacionException();
        }
    }
}
