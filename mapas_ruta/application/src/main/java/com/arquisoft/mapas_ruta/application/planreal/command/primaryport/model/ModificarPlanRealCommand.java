package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ModificarPlanRealCommand(
        UUID planReal,
        Integer tiempoRealDias,
        String descripcionReal
) {

    public static ModificarPlanRealCommand crear(UUID planReal, Integer tiempoRealDias, String descripcionReal) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(planReal,
                MapasRutaFields.PlanReal.PLAN_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO, result);

        if (ValidatorObjeto.noNulo(tiempoRealDias,
                MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_REQUERIDO, result)) {
            ValidatorNumero.valorMinimo(tiempoRealDias, MapasRutaLimits.PlanReal.TIEMPO_REAL_DIAS_MIN,
                    MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS,
                    MapasRutaCodes.PlanReal.PLAN_REAL_TIEMPO_INVALIDO, result);
        }

        var descripcionRecortada = UtilTexto.aplicarTrim(descripcionReal);
        ValidatorLongitud.longitudMaxima(descripcionRecortada,
                MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX,
                MapasRutaFields.PlanReal.DESCRIPCION_REAL,
                MapasRutaCodes.PlanReal.PLAN_REAL_DESCRIPCION_DEMASIADO_LARGA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarPlanRealCommand(planReal, tiempoRealDias, descripcionRecortada);
    }
}
