package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.validator.RemoverPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.model.ExistenciaPlanReal;
import com.arquisoft.mapas_ruta.domain.planreal.rules.PlanRealExisteRule;
import com.arquisoft.mapas_ruta.domain.planreal.rules.impl.PlanRealExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverPlanRealValidatorImpl implements RemoverPlanRealValidator {

    private final PlanRealExisteRule planRealExisteRule;

    public RemoverPlanRealValidatorImpl() {
        this.planRealExisteRule = new PlanRealExisteRuleImpl();
    }

    @Override
    public void validar(RemocionPlanRealDomain remocion, boolean planExiste) {
        planRealExisteRule.validar(new ExistenciaPlanReal(remocion.getPlanReal(), planExiste));
    }
}
