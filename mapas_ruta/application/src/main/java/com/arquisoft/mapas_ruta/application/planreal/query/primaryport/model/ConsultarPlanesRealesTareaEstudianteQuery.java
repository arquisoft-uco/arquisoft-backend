package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarPlanesRealesTareaEstudianteQuery(
        UUID tarea,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarPlanesRealesTareaEstudianteQuery crear(
            UUID tarea, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.PlanReal.TAREA,
                MapasRutaCodes.PlanReal.PLAN_REAL_TAREA_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarPlanesRealesTareaEstudianteQuery(tarea, criterio);
    }
}
