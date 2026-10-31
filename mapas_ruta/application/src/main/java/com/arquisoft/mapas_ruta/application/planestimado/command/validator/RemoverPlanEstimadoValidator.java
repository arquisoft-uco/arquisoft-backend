package com.arquisoft.mapas_ruta.application.planestimado.command.validator;

import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;

public interface RemoverPlanEstimadoValidator {

    void validar(RemocionPlanEstimadoDomain remocion, boolean planExiste);
}
