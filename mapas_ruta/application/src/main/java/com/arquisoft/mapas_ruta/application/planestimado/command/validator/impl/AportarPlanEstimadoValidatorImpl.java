package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AportarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.model.EstadoTareaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.model.ExistenciaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoExisteRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.TareaAceptaPlanEstimadoRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.PlanEstimadoExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.TareaAceptaPlanEstimadoRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import org.springframework.stereotype.Component;

@Component
public class AportarPlanEstimadoValidatorImpl implements AportarPlanEstimadoValidator {

    private final PlanEstimadoExisteRule planEstimadoExisteRule;
    private final TareaAceptaPlanEstimadoRule tareaAceptaPlanEstimadoRule;

    public AportarPlanEstimadoValidatorImpl() {
        this.planEstimadoExisteRule = new PlanEstimadoExisteRuleImpl();
        this.tareaAceptaPlanEstimadoRule = new TareaAceptaPlanEstimadoRuleImpl();
    }

    @Override
    public void validar(AportePlanEstimadoDomain aporte, boolean planExiste, TareaDomain tarea) {
        planEstimadoExisteRule.validar(new ExistenciaPlanEstimado(aporte.getPlanEstimado(), planExiste));
        tareaAceptaPlanEstimadoRule.validar(new EstadoTareaPlanEstimado(
                tarea.getId(), tarea.getDescripcion(), tarea.getEstadoItem()));
    }
}
