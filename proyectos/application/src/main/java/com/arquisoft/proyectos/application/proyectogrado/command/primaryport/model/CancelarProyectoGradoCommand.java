package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CancelarProyectoGradoCommand(
        UUID proyectoGrado,
        UUID coordinador,
        String motivo
) {

    public static CancelarProyectoGradoCommand crear(UUID proyectoGrado, UUID coordinador, String motivo) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.ProyectoGrado.COORDINADOR,
                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(motivo,
                ProyectosFields.ProyectoGrado.MOTIVO,
                ProyectosCodes.ProyectoGrado.MOTIVO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(motivo, ProyectosLimits.ProyectoGrado.MOTIVO_MAX,
                    ProyectosFields.ProyectoGrado.MOTIVO,
                    ProyectosCodes.ProyectoGrado.MOTIVO_LONGITUD_MAXIMA, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new CancelarProyectoGradoCommand(proyectoGrado, coordinador, motivo);
    }
}
