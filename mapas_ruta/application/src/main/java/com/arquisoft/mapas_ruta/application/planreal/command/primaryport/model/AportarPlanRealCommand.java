package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record AportarPlanRealCommand(
        UUID planReal,
        String descripcionReal
) {

    public static AportarPlanRealCommand crear(UUID planReal, String descripcionReal) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result);

        var descripcionRecortada = UtilTexto.aplicarTrim(descripcionReal);
        if (ValidatorTexto.noEnBlanco(descripcionRecortada,
                MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(descripcionRecortada,
                    MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX,
                    MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                    MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AportarPlanRealCommand(planReal, descripcionRecortada);
    }
}
