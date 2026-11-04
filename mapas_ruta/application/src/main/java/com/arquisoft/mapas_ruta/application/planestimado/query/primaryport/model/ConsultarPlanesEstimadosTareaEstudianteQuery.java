package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarPlanesEstimadosTareaEstudianteQuery(
        UUID tarea,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarPlanesEstimadosTareaEstudianteQuery crear(
            UUID tarea, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanEstimado.TAREA,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarPlanesEstimadosTareaEstudianteQuery(tarea, criterio);
    }
}
