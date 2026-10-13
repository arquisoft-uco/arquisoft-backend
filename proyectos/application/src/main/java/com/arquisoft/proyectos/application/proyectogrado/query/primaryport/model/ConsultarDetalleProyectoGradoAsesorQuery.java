package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarDetalleProyectoGradoAsesorQuery(UUID proyectoGrado, UUID asesor) {

    public static ConsultarDetalleProyectoGradoAsesorQuery crear(UUID proyectoGrado, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                ProyectosFields.ProyectoGrado.ASESOR,
                ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarDetalleProyectoGradoAsesorQuery(proyectoGrado, asesor);
    }
}
