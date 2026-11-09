package com.arquisoft.mapas_ruta.application.planestimado.command.validator;

import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;

public interface AportarPlanEstimadoValidator {

    void validar(AportePlanEstimadoDomain aporte, boolean planExiste);
}
