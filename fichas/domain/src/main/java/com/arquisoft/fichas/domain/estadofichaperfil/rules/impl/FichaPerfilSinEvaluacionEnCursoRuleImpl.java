package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilConEvaluacionEnCursoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesEnCursoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilSinEvaluacionEnCursoRule;

public class FichaPerfilSinEvaluacionEnCursoRuleImpl implements FichaPerfilSinEvaluacionEnCursoRule {

    @Override
    public void validar(EvaluacionesEnCursoFicha evaluaciones) {
        if (evaluaciones.estadoActual() == EstadoFicha.DISPONIBLE_PARA_EVALUACION && evaluaciones.enEvaluacion() > 0) {
            throw new FichaPerfilConEvaluacionEnCursoException(evaluaciones.fichaPerfil(), evaluaciones.enEvaluacion());
        }
    }
}
