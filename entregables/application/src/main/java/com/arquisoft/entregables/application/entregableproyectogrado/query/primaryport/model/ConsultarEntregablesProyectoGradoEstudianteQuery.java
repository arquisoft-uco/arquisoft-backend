package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEntregablesProyectoGradoEstudianteQuery(
        UUID estudiante,
        UUID proyectoGrado,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEntregablesProyectoGradoEstudianteQuery crear(
            UUID estudiante, UUID proyectoGrado, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                EntregablesFields.EntregableProyectoGrado.ESTUDIANTE,
                EntregablesCodes.EntregableProyectoGrado.ESTUDIANTE_REQUERIDO, result);
        ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.EntregableProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEntregablesProyectoGradoEstudianteQuery(estudiante, proyectoGrado, criterio);
    }
}
