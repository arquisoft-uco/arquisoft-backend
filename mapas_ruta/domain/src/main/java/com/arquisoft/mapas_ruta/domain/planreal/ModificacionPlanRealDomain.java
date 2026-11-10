package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class ModificacionPlanRealDomain {

    private UUID planReal;
    private int tiempoRealDias;
    private String descripcionReal;

    private ModificacionPlanRealDomain() {}

    public static ModificacionPlanRealDomain crear(UUID planReal, Integer tiempoRealDias, String descripcionReal) {
        var modificacion = new ModificacionPlanRealDomain();
        var result = new ValidationResult();

        modificacion.setPlanReal(planReal, result);
        modificacion.setTiempoRealDias(tiempoRealDias, result);
        modificacion.setDescripcionReal(descripcionReal, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setPlanReal(UUID planReal, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result)) {
            return;
        }
        this.planReal = planReal;
    }

    private void setTiempoRealDias(Integer tiempoRealDias, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tiempoRealDias,
                MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(tiempoRealDias, MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN,
                MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_INVALIDO, result)) {
            return;
        }
        this.tiempoRealDias = tiempoRealDias;
    }

    private void setDescripcionReal(String descripcionReal, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcionReal);
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

    public int getTiempoRealDias() {
        return tiempoRealDias;
    }

    public String getDescripcionReal() {
        return UtilObjeto.esNulo(descripcionReal) ? UtilTexto.VACIO : descripcionReal;
    }
}
