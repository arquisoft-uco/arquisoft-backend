package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AportarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoExisteRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.PlanEstimadoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AportarPlanEstimadoValidatorImpl implements AportarPlanEstimadoValidator {

    private final PlanEstimadoExisteRule planEstimadoExisteRule;

    public AportarPlanEstimadoValidatorImpl() {
        this.planEstimadoExisteRule = new PlanEstimadoExisteRuleImpl();
    }

    @Override
    public void validar(AportePlanEstimadoDomain aporte, boolean planExiste) {
        planEstimadoExisteRule.validar(new ExistenciaPlanEstimado(aporte.getPlanEstimado(), planExiste));
    }
}
