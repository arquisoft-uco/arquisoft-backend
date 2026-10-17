package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteRegistroRule;

public class EvaluacionAdmiteRegistroRuleImpl implements EvaluacionAdmiteRegistroRule {

    @Override
    public void validar(EstadoRegistroEvaluacion estadoRegistro) {
        if (estadoRegistro.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new EvaluacionFinalizadaException();
        }
    }
}
