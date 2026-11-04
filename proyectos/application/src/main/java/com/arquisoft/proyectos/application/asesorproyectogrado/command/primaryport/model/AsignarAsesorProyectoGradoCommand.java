package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AsignarAsesorProyectoGradoCommand(
        UUID proyectoGrado,
        UUID coordinador,
        UUID asesor
) {

    public static AsignarAsesorProyectoGradoCommand crear(UUID proyectoGrado, UUID coordinador, String asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.AsesorProyectoGrado.COORDINADOR,
                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(asesor,
                ProyectosFields.AsesorProyectoGrado.ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(asesor,
                    ProyectosFields.AsesorProyectoGrado.ASESOR,
                    ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_INVALIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarAsesorProyectoGradoCommand(proyectoGrado, coordinador, UtilUUID.generarUUIDDesdeTexto(asesor));
    }
}
