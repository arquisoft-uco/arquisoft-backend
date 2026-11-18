package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarAsesoresActualesProyectoGradoEstudianteQuery(UUID estudiante) {

    public static ConsultarAsesoresActualesProyectoGradoEstudianteQuery crear(UUID estudiante) {
        var result = new ValidationResult();
        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.AsesorProyectoGrado.ESTUDIANTE,
                ProyectosCodes.AsesorProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();
        return new ConsultarAsesoresActualesProyectoGradoEstudianteQuery(estudiante);
    }
}
