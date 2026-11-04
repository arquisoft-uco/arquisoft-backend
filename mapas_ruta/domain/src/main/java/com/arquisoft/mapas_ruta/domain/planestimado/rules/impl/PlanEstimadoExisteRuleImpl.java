package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoExisteRule;

public class PlanEstimadoExisteRuleImpl implements PlanEstimadoExisteRule {

    @Override
    public void validar(ExistenciaPlanEstimado existencia) {
        if (!existencia.existe()) {
            throw new PlanEstimadoNoEncontradoException(existencia.planEstimado());
        }
    }
}
