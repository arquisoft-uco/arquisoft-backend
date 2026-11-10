package com.arquisoft.mapas_ruta.domain.planreal.rules.impl;

import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planreal.model.ExistenciaPlanReal;
import com.arquisoft.mapas_ruta.domain.planreal.rules.PlanRealExisteRule;

public class PlanRealExisteRuleImpl implements PlanRealExisteRule {

    @Override
    public void validar(ExistenciaPlanReal existencia) {
        if (!existencia.existe()) {
            throw new PlanRealNoEncontradoException(existencia.planReal());
        }
    }
}
