package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
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
                MapasRutaFields.ProyectoGrado.ID,
                MapasRutaCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estadoNuevo,
                MapasRutaFields.ProyectoGrado.ESTADO_PROYECTO,
                MapasRutaCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                MapasRutaFields.ProyectoGrado.OCURRIDO_EN,
                MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarEstadoProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), estadoNuevo, ocurridoEn);
    }
}
