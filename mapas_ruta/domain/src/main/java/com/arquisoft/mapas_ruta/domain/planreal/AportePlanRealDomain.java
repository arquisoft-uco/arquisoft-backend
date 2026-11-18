package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class AportePlanRealDomain {

    private UUID planReal;
    private String descripcionReal;

    private AportePlanRealDomain() {}

    public static AportePlanRealDomain crear(UUID planReal, String descripcionReal) {
        var aporte = new AportePlanRealDomain();
        var result = new ValidationResult();

        aporte.setPlanReal(planReal, result);
        aporte.setDescripcionReal(descripcionReal, result);

        result.lanzarSiTieneErrores();
        return aporte;
    }

    private void setPlanReal(UUID planReal, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result)) {
            return;
        }
        this.planReal = planReal;
    }

    private void setDescripcionReal(String descripcionReal, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcionReal);
        if (!ValidatorTexto.noEnBlanco(recortada,
                MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX,
                MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcionReal = recortada;
    }

    public UUID getPlanReal() {
        return planReal;
    }

    public String getDescripcionReal() {
        return UtilObjeto.esNulo(descripcionReal) ? UtilTexto.VACIO : descripcionReal;
    }
}
