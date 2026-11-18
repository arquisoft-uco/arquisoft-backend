package com.arquisoft.mapas_ruta.application.planreal.command.validator;

import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;

public interface AportarPlanRealValidator {

    void validar(AportePlanRealDomain aporte, boolean planExiste);
}
