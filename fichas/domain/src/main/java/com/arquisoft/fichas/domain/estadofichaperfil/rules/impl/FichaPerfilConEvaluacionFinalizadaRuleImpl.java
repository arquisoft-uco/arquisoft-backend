package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEvaluacionFinalizadaException;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEvaluacionFinalizadaRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;

public class FichaPerfilConEvaluacionFinalizadaRuleImpl implements FichaPerfilConEvaluacionFinalizadaRule {

    @Override
    public void validar(ResumenEvaluacionesFicha resumen) {
        if (resumen.finalizadas() == 0) {
            throw new FichaPerfilSinEvaluacionFinalizadaException(resumen.fichaPerfil());
        }
    }
}
