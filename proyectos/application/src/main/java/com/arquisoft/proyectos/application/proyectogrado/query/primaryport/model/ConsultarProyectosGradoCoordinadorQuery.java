package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarProyectosGradoCoordinadorQuery(
        UUID coordinador,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarProyectosGradoCoordinadorQuery crear(
            UUID coordinador, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.ProyectoGrado.COORDINADOR,
                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarProyectosGradoCoordinadorQuery(coordinador, criterio);
    }
}
