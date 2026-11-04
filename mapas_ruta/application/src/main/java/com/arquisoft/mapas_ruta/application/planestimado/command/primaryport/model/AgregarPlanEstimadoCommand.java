package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record AgregarPlanEstimadoCommand(
        UUID tarea,
        Integer tiempoEstimadoDias,
        String descripcionEstimado
) {

    public static AgregarPlanEstimadoCommand crear(UUID tarea, Integer tiempoEstimadoDias, String descripcionEstimado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanEstimado.TAREA,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_REQUERIDA, result);

        if (ValidatorObjeto.noNulo(tiempoEstimadoDias,
                MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_REQUERIDO, result)) {
            ValidatorNumero.valorMinimo(tiempoEstimadoDias, MapasRutaLimits.PlanEstimado.TIEMPO_ESTIMADO_DIAS_MIN,
                    MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS,
                    MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_INVALIDO, result);
        }

        var descripcionRecortada = UtilTexto.aplicarTrim(descripcionEstimado);
        ValidatorLongitud.longitudMaxima(descripcionRecortada,
                MapasRutaLimits.PlanEstimado.DESCRIPCION_ESTIMADO_MAX,
                MapasRutaFields.PlanEstimado.DESCRIPCION_ESTIMADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DESCRIPCION_DEMASIADO_LARGA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarPlanEstimadoCommand(tarea, tiempoEstimadoDias, descripcionRecortada);
    }
}
