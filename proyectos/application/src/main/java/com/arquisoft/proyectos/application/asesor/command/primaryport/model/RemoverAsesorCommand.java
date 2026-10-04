package com.arquisoft.proyectos.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RemoverAsesorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static RemoverAsesorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, ProyectosFields.Asesor.ID, ProyectosCodes.Asesor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, ProyectosFields.Asesor.IDENTIFICADOR,
                ProyectosCodes.Asesor.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, ProyectosFields.Asesor.NOMBRE,
                ProyectosCodes.Asesor.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, ProyectosFields.Asesor.EMAIL,
                ProyectosCodes.Asesor.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, ProyectosFields.Asesor.OCURRIDO_EN,
                ProyectosCodes.Asesor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
