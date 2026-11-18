package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.validator.AportarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.model.ExistenciaPlanReal;
import com.arquisoft.mapas_ruta.domain.planreal.rules.PlanRealExisteRule;
import com.arquisoft.mapas_ruta.domain.planreal.rules.impl.PlanRealExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AportarPlanRealValidatorImpl implements AportarPlanRealValidator {

    private final PlanRealExisteRule planRealExisteRule;

    public AportarPlanRealValidatorImpl() {
        this.planRealExisteRule = new PlanRealExisteRuleImpl();
    }

    @Override
    public void validar(AportePlanRealDomain aporte, boolean planExiste) {
        planRealExisteRule.validar(new ExistenciaPlanReal(aporte.getPlanReal(), planExiste));
    }
}
