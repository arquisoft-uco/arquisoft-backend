package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionPlanRealDomain {

    private UUID planReal;

    private RemocionPlanRealDomain() {}

    public static RemocionPlanRealDomain crear(UUID planReal) {
        var remocion = new RemocionPlanRealDomain();
        var result = new ValidationResult();

        remocion.setPlanReal(planReal, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setPlanReal(UUID planReal, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result)) {
            return;
        }
        this.planReal = planReal;
    }

    public UUID getPlanReal() {
        return planReal;
    }
}
