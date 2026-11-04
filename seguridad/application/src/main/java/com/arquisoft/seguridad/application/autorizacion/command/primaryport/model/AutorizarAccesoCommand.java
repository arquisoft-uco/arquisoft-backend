package com.arquisoft.seguridad.application.autorizacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record AutorizarAccesoCommand(String metodo, String recurso, String credencial, String emisor,
                                     String correlacion) {

    public AutorizarAccesoCommand {
        metodo = UtilTexto.aplicarTrim(metodo);
        recurso = UtilTexto.aplicarTrim(recurso);
        credencial = UtilTexto.aplicarTrim(credencial);
        emisor = UtilTexto.aplicarTrim(emisor);
        correlacion = UtilTexto.aplicarTrim(correlacion);
    }

    public static AutorizarAccesoCommand crear(String metodo, String recurso, String credencial, String emisor,
                                               String correlacion) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(metodo,
                SeguridadFields.Autorizacion.METODO,
                SeguridadCodes.Autorizacion.ACCESO_METODO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(metodo, SeguridadLimits.Acceso.METODO_MAX,
                    SeguridadFields.Autorizacion.METODO,
                    SeguridadCodes.Autorizacion.ACCESO_METODO_EXCEDE_LIMITE, result);
        }
        if (ValidatorTexto.noEnBlanco(recurso,
                SeguridadFields.Autorizacion.RECURSO,
                SeguridadCodes.Autorizacion.ACCESO_RECURSO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(recurso, SeguridadLimits.Acceso.RECURSO_MAX,
                    SeguridadFields.Autorizacion.RECURSO,
                    SeguridadCodes.Autorizacion.ACCESO_RECURSO_EXCEDE_LIMITE, result);
        }
        if (ValidatorTexto.noEnBlanco(credencial,
                SeguridadFields.Autorizacion.CREDENCIAL,
                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_REQUERIDA, result)) {
            ValidatorLongitud.longitudMaxima(credencial, SeguridadLimits.Acceso.CREDENCIAL_MAX,
                    SeguridadFields.Autorizacion.CREDENCIAL,
                    SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_EXCEDE_LIMITE, result);
        }
        if (ValidatorTexto.noEnBlanco(emisor,
                SeguridadFields.Autorizacion.EMISOR,
                SeguridadCodes.Autorizacion.ACCESO_EMISOR_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(emisor, SeguridadLimits.Acceso.EMISOR_MAX,
                    SeguridadFields.Autorizacion.EMISOR,
                    SeguridadCodes.Autorizacion.ACCESO_EMISOR_EXCEDE_LIMITE, result);
        }
        if (!UtilTexto.esVacioONulo(correlacion)) {
            ValidatorLongitud.longitudMaxima(correlacion, SeguridadLimits.Acceso.CORRELACION_MAX,
                    SeguridadFields.Autorizacion.CORRELACION,
                    SeguridadCodes.Autorizacion.ACCESO_CORRELACION_EXCEDE_LIMITE, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AutorizarAccesoCommand(metodo, recurso, credencial, emisor, correlacion);
    }

    @Override
    public String toString() {
        return "AutorizarAccesoCommand[metodo=" + metodo + ", recurso=" + recurso
                + ", credencial=***, emisor=" + emisor + "]";
    }
}
