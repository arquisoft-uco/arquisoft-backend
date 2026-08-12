package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;

public class RepresentantePropietarioEvaluacionRuleImpl implements RepresentantePropietarioEvaluacionRule {

    @Override
    public void validar(PropiedadEvaluacionFicha propiedad) {
        if (!propiedad.esPropietario()) {
            throw new EvaluacionFichaNoPropiaException(propiedad.evaluacionFichaPerfil());
        }
    }
}
