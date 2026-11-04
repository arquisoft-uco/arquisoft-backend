package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEvaluacionFinalizadaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesFinalizadasFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEvaluacionFinalizadaRule;

public class FichaPerfilConEvaluacionFinalizadaRuleImpl implements FichaPerfilConEvaluacionFinalizadaRule {

    @Override
    public void validar(EvaluacionesFinalizadasFicha evaluaciones) {
        if (evaluaciones.resumen().finalizadas() == 0) {
            throw new FichaPerfilSinEvaluacionFinalizadaException(
                    evaluaciones.resumen().fichaPerfil(), evaluaciones.tituloProyecto());
        }
    }
}
