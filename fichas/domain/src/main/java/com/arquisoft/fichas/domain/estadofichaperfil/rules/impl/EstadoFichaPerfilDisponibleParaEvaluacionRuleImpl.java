package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilDisponibleParaEvaluacionRule;

public class EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl implements EstadoFichaPerfilDisponibleParaEvaluacionRule {

    @Override
    public void validar(EstadoActualFicha estado) {
        if (estado.estadoActual() != EstadoFicha.DISPONIBLE_PARA_EVALUACION) {
            throw new FichaPerfilNoDisponibleParaEvaluacionException(estado.estadoActual());
        }
    }
}
