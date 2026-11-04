package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.validator.RemoverPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoExisteRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.PlanEstimadoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverPlanEstimadoValidatorImpl implements RemoverPlanEstimadoValidator {

    private final PlanEstimadoExisteRule planEstimadoExisteRule;

    public RemoverPlanEstimadoValidatorImpl() {
        this.planEstimadoExisteRule = new PlanEstimadoExisteRuleImpl();
    }

    @Override
    public void validar(RemocionPlanEstimadoDomain remocion, boolean planExiste) {
        planEstimadoExisteRule.validar(new ExistenciaPlanEstimado(remocion.getPlanEstimado(), planExiste));
    }
}
