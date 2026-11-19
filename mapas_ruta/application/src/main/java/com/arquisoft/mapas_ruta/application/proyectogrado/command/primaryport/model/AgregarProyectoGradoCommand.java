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

public record AgregarProyectoGradoCommand(
        UUID proyectoGrado, UUID fichaPerfil, String tituloProyecto, UUID coordinador, Instant ocurridoEn) {

    public static AgregarProyectoGradoCommand crear(
            String proyectoGrado, String fichaPerfil, String tituloProyecto, String coordinador, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                MapasRutaFields.ProyectoGrado.ID,
                MapasRutaCodes.ProyectoGrado.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(fichaPerfil,
                MapasRutaFields.ProyectoGrado.FICHA_PERFIL,
                MapasRutaCodes.ProyectoGrado.FICHA_PERFIL_REQUERIDA, result);
        ValidatorTexto.noEnBlanco(tituloProyecto,
                MapasRutaFields.ProyectoGrado.TITULO_PROYECTO,
                MapasRutaCodes.ProyectoGrado.TITULO_PROYECTO_REQUERIDO, result);
        ValidatorUUID.uuidValido(coordinador,
                MapasRutaFields.ProyectoGrado.COORDINADOR,
                MapasRutaCodes.ProyectoGrado.COORDINADOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                MapasRutaFields.ProyectoGrado.OCURRIDO_EN,
                MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                UtilUUID.generarUUIDDesdeTexto(fichaPerfil),
                tituloProyecto,
                UtilUUID.generarUUIDDesdeTexto(coordinador),
                ocurridoEn);
    }
}
