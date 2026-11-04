package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoDuplicadoException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.DisponibilidadPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoUnicoPorTareaRule;

public class PlanEstimadoUnicoPorTareaRuleImpl implements PlanEstimadoUnicoPorTareaRule {

    @Override
    public void validar(DisponibilidadPlanEstimado disponibilidad) {
        if (disponibilidad.existe()) {
            throw new PlanEstimadoDuplicadoException(disponibilidad.descripcionTarea(), disponibilidad.tarea());
        }
    }
}
