package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class GeneracionPlanRealDomain {

    private UUID tarea;
    private Instant finalizadaEn;

    private GeneracionPlanRealDomain() {}

    public static GeneracionPlanRealDomain crear(UUID tarea, Instant finalizadaEn) {
        var generacion = new GeneracionPlanRealDomain();
        var result = new ValidationResult();

        generacion.setTarea(tarea, result);
        generacion.setFinalizadaEn(finalizadaEn, result);

        result.lanzarSiTieneErrores();
        return generacion;
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanReal.TAREA,
                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    private void setFinalizadaEn(Instant finalizadaEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(finalizadaEn,
                MapasRutaFields.PlanReal.FINALIZADA_EN,
                MapasRutaCodes.PlanReal.PLAN_REAL_FINALIZADA_EN_REQUERIDA, result)) {
            return;
        }
        this.finalizadaEn = finalizadaEn;
    }

    public UUID getTarea() {
        return tarea;
    }

    public Instant getFinalizadaEn() {
        return finalizadaEn;
    }
}
