package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
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
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoNuevo,
                ArtefactosFields.ProyectoGrado.ESTADO_PROYECTO,
                ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.ProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarEstadoProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), estadoNuevo, ocurridoEn);
    }
}
