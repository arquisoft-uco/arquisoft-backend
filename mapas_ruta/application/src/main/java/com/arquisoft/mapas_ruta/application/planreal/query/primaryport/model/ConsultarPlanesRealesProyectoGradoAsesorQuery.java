package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarPlanesRealesProyectoGradoAsesorQuery(
        UUID proyectoGrado,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarPlanesRealesProyectoGradoAsesorQuery crear(
            UUID proyectoGrado, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                MapasRutaFields.PlanReal.PROYECTO_GRADO,
                MapasRutaCodes.PlanReal.PLAN_REAL_PROYECTO_GRADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarPlanesRealesProyectoGradoAsesorQuery(proyectoGrado, criterio);
    }
}
