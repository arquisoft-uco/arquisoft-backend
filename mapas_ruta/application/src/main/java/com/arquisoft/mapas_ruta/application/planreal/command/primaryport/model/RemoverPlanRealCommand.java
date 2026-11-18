package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverPlanRealCommand(
        UUID planReal
) {

    public static RemoverPlanRealCommand crear(UUID planReal) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverPlanRealCommand(planReal);
    }
}
