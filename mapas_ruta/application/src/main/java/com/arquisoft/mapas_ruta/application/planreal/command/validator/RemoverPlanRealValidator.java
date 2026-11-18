package com.arquisoft.mapas_ruta.application.planreal.command.validator;

import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;

public interface RemoverPlanRealValidator {

    void validar(RemocionPlanRealDomain remocion, boolean planExiste);
}
