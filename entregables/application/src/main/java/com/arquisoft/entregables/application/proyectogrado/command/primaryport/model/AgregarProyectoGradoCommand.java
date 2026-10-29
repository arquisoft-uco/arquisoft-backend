package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
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
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result);
        if (ValidatorTexto.noEnBlanco(nombreRecortado,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado,
                    EntregablesLimits.ProyectoGrado.NOMBRE_MAX,
                    EntregablesFields.ProyectoGrado.NOMBRE,
                    EntregablesCodes.ProyectoGrado.NOMBRE_LONGITUD_MAXIMA, result);
        }
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado), nombreRecortado);
    }
}
