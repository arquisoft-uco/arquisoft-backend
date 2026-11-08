package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarPlanesEstimadosProyectoGradoAsesorQuery(
        UUID proyectoGrado,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarPlanesEstimadosProyectoGradoAsesorQuery crear(
            UUID proyectoGrado, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                MapasRutaFields.PlanEstimado.PROYECTO_GRADO,
                MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_PROYECTO_GRADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarPlanesEstimadosProyectoGradoAsesorQuery(proyectoGrado, criterio);
    }
}
