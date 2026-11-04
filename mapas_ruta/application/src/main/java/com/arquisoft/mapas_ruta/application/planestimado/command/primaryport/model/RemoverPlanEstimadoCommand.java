package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverPlanEstimadoCommand(
        UUID planEstimado
) {

    public static RemoverPlanEstimadoCommand crear(UUID planEstimado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(planEstimado,
                MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverPlanEstimadoCommand(planEstimado);
    }
}
