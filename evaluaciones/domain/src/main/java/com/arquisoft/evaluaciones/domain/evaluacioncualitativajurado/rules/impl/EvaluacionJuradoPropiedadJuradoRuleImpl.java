package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;

public class EvaluacionJuradoPropiedadJuradoRuleImpl implements EvaluacionJuradoPropiedadJuradoRule {

    @Override
    public void validar(PropiedadEvaluacionJurado propiedad) {
        if (!propiedad.juradoAsignado().equals(propiedad.juradoSolicitante())) {
            throw new EvaluacionJuradoNoPerteneceJuradoException(propiedad.evaluacionJurado());
        }
    }
}
