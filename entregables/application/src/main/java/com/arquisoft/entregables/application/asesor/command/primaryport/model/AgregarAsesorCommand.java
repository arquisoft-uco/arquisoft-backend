package com.arquisoft.entregables.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarAsesorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarAsesorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, EntregablesFields.Asesor.ID,
                EntregablesCodes.Asesor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, EntregablesFields.Asesor.IDENTIFICADOR,
                EntregablesCodes.Asesor.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, EntregablesFields.Asesor.NOMBRE,
                EntregablesCodes.Asesor.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, EntregablesFields.Asesor.EMAIL,
                EntregablesCodes.Asesor.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EntregablesFields.Asesor.OCURRIDO_EN,
                EntregablesCodes.Asesor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
