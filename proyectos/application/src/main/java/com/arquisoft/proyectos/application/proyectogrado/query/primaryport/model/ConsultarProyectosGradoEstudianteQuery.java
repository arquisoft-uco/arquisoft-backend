package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarProyectosGradoEstudianteQuery(UUID estudiante) {

    public static ConsultarProyectosGradoEstudianteQuery crear(UUID estudiante) {
        var result = new ValidationResult();
        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.ProyectoGrado.ESTUDIANTE,
                ProyectosCodes.ProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();
        return new ConsultarProyectosGradoEstudianteQuery(estudiante);
    }
}
