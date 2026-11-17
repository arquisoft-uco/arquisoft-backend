package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriterioEvaluacionCualitativaJuradoSinCambioException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.CriterioActualVsNuevo;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.CriterioEvaluacionCualitativaJuradoDistintoRule;

public class CriterioEvaluacionCualitativaJuradoDistintoRuleImpl
        implements CriterioEvaluacionCualitativaJuradoDistintoRule {

    @Override
    public void validar(CriterioActualVsNuevo criterios) {
        if (criterios.criterioActual().equals(criterios.criterioNuevo())) {
            throw new CriterioEvaluacionCualitativaJuradoSinCambioException(
                    criterios.evaluacionCualitativaJurado(), criterios.criterioNuevo());
        }
    }
}
