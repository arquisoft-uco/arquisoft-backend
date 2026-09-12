package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;
import com.arquisoft.shared.util.UtilObjeto;

public class EvaluacionJuradoPropiedadJuradoRuleImpl implements EvaluacionJuradoPropiedadJuradoRule {

    @Override
    public void validar(PropiedadEvaluacionJurado propiedad) {
        if (UtilObjeto.esNulo(propiedad.propietario()) || !propiedad.propietario().equals(propiedad.actor())) {
            throw new EvaluacionJuradoNoPerteneceJuradoException();
        }
    }
}
