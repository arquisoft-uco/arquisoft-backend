package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;

public class EvaluacionFichaExisteRuleImpl implements EvaluacionFichaExisteRule {

    @Override
    public void validar(ExistenciaEvaluacionFicha existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionFichaPerfilNoEncontradaException(existencia.evaluacionFichaPerfil());
        }
    }
}
