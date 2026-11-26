package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionNoDescartableException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionDescartableRule;

public class EvaluacionDescartableRuleImpl implements EvaluacionDescartableRule {

    @Override
    public void validar(UltimoEstadoEvaluacion ultimo) {
        if (ultimo.ultimoEstado() != EstadoEvaluacion.EN_EVALUACION) {
            throw new EvaluacionNoDescartableException(ultimo.evaluacionFichaPerfil(), ultimo.ultimoEstado());
        }
    }
}
