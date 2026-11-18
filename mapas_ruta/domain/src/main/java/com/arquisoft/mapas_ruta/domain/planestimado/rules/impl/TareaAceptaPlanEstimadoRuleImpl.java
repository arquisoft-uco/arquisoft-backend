package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoTareaFinalizadaException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.EstadoTareaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.TareaAceptaPlanEstimadoRule;

public class TareaAceptaPlanEstimadoRuleImpl implements TareaAceptaPlanEstimadoRule {

    @Override
    public void validar(EstadoTareaPlanEstimado estadoTarea) {
        if (!estadoTarea.estadoItem().permitePlanEstimado()) {
            throw new PlanEstimadoTareaFinalizadaException(estadoTarea.descripcionTarea(), estadoTarea.tarea());
        }
    }
}
