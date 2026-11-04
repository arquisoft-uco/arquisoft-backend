package com.arquisoft.mapas_ruta.application.planestimado.command.validator;

import com.arquisoft.mapas_ruta.domain.planestimado.ModificacionPlanEstimadoDomain;

public interface ModificarPlanEstimadoValidator {

    void validar(ModificacionPlanEstimadoDomain modificacion, boolean planExiste);
}
