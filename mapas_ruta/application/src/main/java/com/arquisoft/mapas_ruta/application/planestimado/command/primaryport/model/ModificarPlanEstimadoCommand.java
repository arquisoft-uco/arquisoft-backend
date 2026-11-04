package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ModificarPlanEstimadoCommand(
        UUID planEstimado,
        String descripcionEstimado
) {

    public static ModificarPlanEstimadoCommand crear(UUID planEstimado, String descripcionEstimado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(planEstimado,
                MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO, result);

        var descripcionRecortada = UtilTexto.aplicarTrim(descripcionEstimado);
        ValidatorLongitud.longitudMaxima(descripcionRecortada,
                MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX,
                MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarPlanEstimadoCommand(planEstimado, descripcionRecortada);
    }
}
