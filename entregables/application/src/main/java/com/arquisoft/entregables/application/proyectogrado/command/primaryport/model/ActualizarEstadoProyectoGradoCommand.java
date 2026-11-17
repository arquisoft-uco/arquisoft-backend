package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record ActualizarEstadoProyectoGradoCommand(UUID proyectoGrado, String estadoNuevo, Instant ocurridoEn) {

    public static ActualizarEstadoProyectoGradoCommand crear(
            String proyectoGrado, String estadoNuevo, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoNuevo,
                EntregablesFields.ProyectoGrado.ESTADO_PROYECTO,
                EntregablesCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                EntregablesFields.ProyectoGrado.OCURRIDO_EN,
                EntregablesCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarEstadoProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), estadoNuevo, ocurridoEn);
    }
}
