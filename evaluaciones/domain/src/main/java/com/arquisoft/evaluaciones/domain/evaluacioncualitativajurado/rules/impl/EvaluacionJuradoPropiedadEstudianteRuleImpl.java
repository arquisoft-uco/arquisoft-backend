package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJuradoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadEstudianteRule;

public class EvaluacionJuradoPropiedadEstudianteRuleImpl implements EvaluacionJuradoPropiedadEstudianteRule {

    @Override
    public void validar(PropiedadEvaluacionJuradoEstudiante propiedad) {
        if (!propiedad.pertenece()) {
            throw new EvaluacionJuradoNoPerteneceEstudianteException(propiedad.evaluacionJurado());
        }
    }
}
