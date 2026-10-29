package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.validator.ModificarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoExisteRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.PlanEstimadoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarPlanEstimadoValidatorImpl implements ModificarPlanEstimadoValidator {

    private final PlanEstimadoExisteRule planEstimadoExisteRule;

    public ModificarPlanEstimadoValidatorImpl() {
        this.planEstimadoExisteRule = new PlanEstimadoExisteRuleImpl();
    }

    @Override
    public void validar(ModificacionPlanEstimadoDomain modificacion, boolean planExiste) {
        planEstimadoExisteRule.validar(new ExistenciaPlanEstimado(modificacion.getPlanEstimado(), planExiste));
    }
}
