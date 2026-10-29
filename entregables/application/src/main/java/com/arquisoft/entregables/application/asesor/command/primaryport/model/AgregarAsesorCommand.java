package com.arquisoft.entregables.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
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
        var identificadorRecortado = UtilTexto.aplicarTrim(identificador);
        var nombreRecortado = UtilTexto.aplicarTrim(nombre);
        var emailRecortado = UtilTexto.aplicarTrim(email);

        ValidatorUUID.uuidValido(id, EntregablesFields.Asesor.ID,
                EntregablesCodes.Asesor.ID_REQUERIDO, result);
        if (ValidatorTexto.noEnBlanco(identificadorRecortado, EntregablesFields.Asesor.IDENTIFICADOR,
                EntregablesCodes.Asesor.IDENTIFICADOR_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(identificadorRecortado, EntregablesLimits.Asesor.IDENTIFICADOR_MAX,
                    EntregablesFields.Asesor.IDENTIFICADOR, EntregablesCodes.Asesor.IDENTIFICADOR_LONGITUD_MAXIMA, result);
        }
        if (ValidatorTexto.noEnBlanco(nombreRecortado, EntregablesFields.Asesor.NOMBRE,
                EntregablesCodes.Asesor.NOMBRE_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(nombreRecortado, EntregablesLimits.Asesor.NOMBRE_MAX,
                    EntregablesFields.Asesor.NOMBRE, EntregablesCodes.Asesor.NOMBRE_LONGITUD_MAXIMA, result);
        }
        if (ValidatorTexto.noEnBlanco(emailRecortado, EntregablesFields.Asesor.EMAIL,
                EntregablesCodes.Asesor.EMAIL_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(emailRecortado, EntregablesLimits.Asesor.EMAIL_MAX,
                    EntregablesFields.Asesor.EMAIL, EntregablesCodes.Asesor.EMAIL_LONGITUD_MAXIMA, result);
        }
        ValidatorObjeto.noNulo(ocurridoEn, EntregablesFields.Asesor.OCURRIDO_EN,
                EntregablesCodes.Asesor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificadorRecortado, nombreRecortado, emailRecortado,
                ocurridoEn);
    }
}
