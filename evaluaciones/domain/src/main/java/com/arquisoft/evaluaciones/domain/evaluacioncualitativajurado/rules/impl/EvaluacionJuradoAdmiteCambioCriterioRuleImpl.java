package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CambioCriterioEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoCambioCriterioEvaluacionCualitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoAdmiteCambioCriterioRule;

public class EvaluacionJuradoAdmiteCambioCriterioRuleImpl implements EvaluacionJuradoAdmiteCambioCriterioRule {

    @Override
    public void validar(EstadoCambioCriterioEvaluacionCualitativaJurado estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new CambioCriterioEvaluacionJuradoFinalizadaException(estado.evaluacionCualitativaJurado());
        }
    }
}
