package com.arquisoft.mapas_ruta.domain.planestimado;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class ModificacionPlanEstimadoDomain {

    private UUID planEstimado;
    private String descripcionEstimado;

    private ModificacionPlanEstimadoDomain() {}

    public static ModificacionPlanEstimadoDomain crear(UUID planEstimado, String descripcionEstimado) {
        var modificacion = new ModificacionPlanEstimadoDomain();
        var result = new ValidationResult();

        modificacion.setPlanEstimado(planEstimado, result);
        modificacion.setDescripcionEstimado(descripcionEstimado, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setPlanEstimado(UUID planEstimado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(planEstimado,
                MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO, result)) {
            return;
        }
        this.planEstimado = planEstimado;
    }

    private void setDescripcionEstimado(String descripcionEstimado, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcionEstimado);
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX,
                MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcionEstimado = recortada;
    }

    public UUID getPlanEstimado() {
        return planEstimado;
    }

    public String getDescripcionEstimado() {
        return UtilObjeto.esNulo(descripcionEstimado) ? UtilTexto.VACIO : descripcionEstimado;
    }
}
