package com.arquisoft.mapas_ruta.application.planreal.command.validator;

import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;

public interface ModificarPlanRealValidator {

    void validar(ModificacionPlanRealDomain modificacion, boolean planExiste);
}
