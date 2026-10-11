package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoAsesorProyectoGradoCommand(
        UUID proyectoGrado,
        UUID asesor,
        UUID coordinador,
        String estadoAsesor
) {

    public static CambiarEstadoAsesorProyectoGradoCommand crear(UUID proyectoGrado, UUID asesor,
                                                                UUID coordinador, String estadoAsesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                ProyectosFields.AsesorProyectoGrado.ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.AsesorProyectoGrado.COORDINADOR,
                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        ValidatorTexto.noEnBlanco(estadoAsesor,
                ProyectosFields.AsesorProyectoGrado.ESTADO_ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoAsesorProyectoGradoCommand(proyectoGrado, asesor, coordinador, estadoAsesor);
    }
}
