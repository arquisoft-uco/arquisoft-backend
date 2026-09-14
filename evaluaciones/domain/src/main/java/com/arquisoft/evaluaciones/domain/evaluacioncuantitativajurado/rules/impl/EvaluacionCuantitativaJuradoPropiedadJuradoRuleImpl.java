package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionCuantitativaJuradoPropiedadJuradoRule;

public class EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl
        implements EvaluacionCuantitativaJuradoPropiedadJuradoRule {

    @Override
    public void validar(PropiedadEvaluacionCuantitativaJuradoJurado propiedad) {
        if (!propiedad.pertenece()) {
            throw new EvaluacionCuantitativaJuradoNoPerteneceJuradoException(propiedad.evaluacionCuantitativaJurado());
        }
    }
}
