package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarProyectoGradoCommand(UUID proyectoGrado, String nombre) {

    public static AgregarProyectoGradoCommand crear(String proyectoGrado, String nombre) {
        var result = new ValidationResult();
        var nombreRecortado = UtilTexto.aplicarTrim(nombre);

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result);
        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                ArtefactosFields.ProyectoGrado.NOMBRE,
                ArtefactosCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    ArtefactosLimits.ProyectoGrado.NOMBRE_MAX,
                    ArtefactosFields.ProyectoGrado.NOMBRE,
                    ArtefactosCodes.ProyectoGrado.NOMBRE_DEMASIADO_LARGO, result);
        }
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado), nombreRecortado);
    }
}
