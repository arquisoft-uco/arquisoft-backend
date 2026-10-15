package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarProyectosGradoAsesorQuery(
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarProyectosGradoAsesorQuery crear(UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                ProyectosFields.ProyectoGrado.ASESOR,
                ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarProyectosGradoAsesorQuery(asesor, criterio);
    }
}
