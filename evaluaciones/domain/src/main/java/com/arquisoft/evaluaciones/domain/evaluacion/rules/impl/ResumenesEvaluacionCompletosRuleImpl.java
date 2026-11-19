package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.ResumenesEvaluacionIncompletosException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ResumenesEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.ResumenesEvaluacionCompletosRule;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;

public class ResumenesEvaluacionCompletosRuleImpl implements ResumenesEvaluacionCompletosRule {

    @Override
    public void validar(ResumenesEvaluacion entrada) {
        if (entrada.asesor().esVacio()) {
            throw new ResumenesEvaluacionIncompletosException(
                    entrada.evaluacion(), EvaluacionKey.ERROR_RESUMENES_ASESOR);
        }
        if (entrada.jurados().size() != entrada.juradosEvaluados()) {
            throw new ResumenesEvaluacionIncompletosException(
                    entrada.evaluacion(), EvaluacionKey.ERROR_RESUMENES_JURADOS);
        }
    }
}
