package com.arquisoft.mapas_ruta.domain.planestimado;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionPlanEstimadoDomain {

    private UUID planEstimado;

    private RemocionPlanEstimadoDomain() {}

    public static RemocionPlanEstimadoDomain crear(UUID planEstimado) {
        var remocion = new RemocionPlanEstimadoDomain();
        var result = new ValidationResult();

        remocion.setPlanEstimado(planEstimado, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setPlanEstimado(UUID planEstimado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(planEstimado,
                MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO, result)) {
            return;
        }
        this.planEstimado = planEstimado;
    }

    public UUID getPlanEstimado() {
        return planEstimado;
    }
}
