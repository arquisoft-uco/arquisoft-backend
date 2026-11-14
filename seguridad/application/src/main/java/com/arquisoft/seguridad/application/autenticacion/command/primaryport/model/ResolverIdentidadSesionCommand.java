package com.arquisoft.seguridad.application.autenticacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record ResolverIdentidadSesionCommand(String credencial) {

    public ResolverIdentidadSesionCommand {
        credencial = UtilTexto.aplicarTrim(credencial);
    }

    public static ResolverIdentidadSesionCommand crear(String credencial) {
        var result = new ValidationResult();
        var recortada = UtilTexto.aplicarTrim(credencial);

        if (ValidatorTexto.noEnBlanco(recortada,
                SeguridadFields.CredencialSesion.CREDENCIAL,
                SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(recortada, SeguridadLimits.CredencialSesion.CREDENCIAL_MAX,
                    SeguridadFields.CredencialSesion.CREDENCIAL,
                    SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_EXCEDE_LIMITE, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ResolverIdentidadSesionCommand(recortada);
    }
}
