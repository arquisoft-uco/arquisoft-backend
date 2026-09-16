package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionCuantitativaJuradoPropiedadJuradoRule;

public class EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl
        implements EvaluacionCuantitativaJuradoPropiedadJuradoRule {

    @Override
    public void validar(PropiedadEvaluacionCuantitativaJuradoJurado propiedad) {
        if (!propiedad.pertenece()) {
            throw new EvaluacionCuantitativaJuradoNoPerteneceJuradoException(propiedad.evaluacionCuantitativaJurado());
        }
    }
}
