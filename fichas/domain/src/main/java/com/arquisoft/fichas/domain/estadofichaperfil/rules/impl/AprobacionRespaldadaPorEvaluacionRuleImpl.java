package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.AprobacionSinEvaluacionAprobatoriaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.RespaldoAprobacionFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.AprobacionRespaldadaPorEvaluacionRule;

public class AprobacionRespaldadaPorEvaluacionRuleImpl implements AprobacionRespaldadaPorEvaluacionRule {

    @Override
    public void validar(RespaldoAprobacionFicha respaldo) {
        if (respaldo.acepta() && respaldo.resumen().aprobatorias() == 0) {
            throw new AprobacionSinEvaluacionAprobatoriaException(respaldo.resumen().fichaPerfil());
        }
    }
}
