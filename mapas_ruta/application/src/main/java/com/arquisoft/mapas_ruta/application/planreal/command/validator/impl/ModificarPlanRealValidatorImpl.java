package com.arquisoft.mapas_ruta.application.planreal.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.validator.ModificarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.model.ExistenciaPlanReal;
import com.arquisoft.mapas_ruta.domain.planreal.rules.PlanRealExisteRule;
import com.arquisoft.mapas_ruta.domain.planreal.rules.impl.PlanRealExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarPlanRealValidatorImpl implements ModificarPlanRealValidator {

    private final PlanRealExisteRule planRealExisteRule;

    public ModificarPlanRealValidatorImpl() {
        this.planRealExisteRule = new PlanRealExisteRuleImpl();
    }

    @Override
    public void validar(ModificacionPlanRealDomain modificacion, boolean planExiste) {
        planRealExisteRule.validar(new ExistenciaPlanReal(modificacion.getPlanReal(), planExiste));
    }
}
