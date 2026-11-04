package com.arquisoft.seguridad.domain.autorizacion;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class AccesoDomain {

    private String metodo;
    private String recurso;
    private String credencial;
    private String emisor;
    private String correlacion;

    private AccesoDomain() {}

    public static AccesoDomain crear(String metodo, String recurso, String credencial, String emisor,
                                     String correlacion) {
        var acceso = new AccesoDomain();
        var result = new ValidationResult();

        acceso.setMetodo(metodo, result);
        acceso.setRecurso(recurso, result);
        acceso.setCredencial(credencial, result);
        acceso.setEmisor(emisor, result);
        acceso.setCorrelacion(correlacion, result);

        result.lanzarSiTieneErrores();
        return acceso;
    }

    private void setMetodo(String metodo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(metodo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SeguridadFields.Autorizacion.METODO,
                SeguridadCodes.Autorizacion.ACCESO_METODO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, SeguridadLimits.Acceso.METODO_MAX,
                SeguridadFields.Autorizacion.METODO,
                SeguridadCodes.Autorizacion.ACCESO_METODO_EXCEDE_LIMITE, result)) {
            return;
        }
        this.metodo = recortado;
    }

    private void setRecurso(String recurso, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(recurso);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SeguridadFields.Autorizacion.RECURSO,
                SeguridadCodes.Autorizacion.ACCESO_RECURSO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, SeguridadLimits.Acceso.RECURSO_MAX,
                SeguridadFields.Autorizacion.RECURSO,
                SeguridadCodes.Autorizacion.ACCESO_RECURSO_EXCEDE_LIMITE, result)) {
            return;
        }
        this.recurso = recortado;
    }

    private void setCredencial(String credencial, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(credencial);
        if (!ValidatorTexto.noEnBlanco(recortada,
                SeguridadFields.Autorizacion.CREDENCIAL,
                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, SeguridadLimits.Acceso.CREDENCIAL_MAX,
                SeguridadFields.Autorizacion.CREDENCIAL,
                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_EXCEDE_LIMITE, result)) {
            return;
        }
        this.credencial = recortada;
    }

    private void setEmisor(String emisor, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(emisor);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SeguridadFields.Autorizacion.EMISOR,
                SeguridadCodes.Autorizacion.ACCESO_EMISOR_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, SeguridadLimits.Acceso.EMISOR_MAX,
                SeguridadFields.Autorizacion.EMISOR,
                SeguridadCodes.Autorizacion.ACCESO_EMISOR_EXCEDE_LIMITE, result)) {
            return;
        }
        this.emisor = recortado;
    }

    private void setCorrelacion(String correlacion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(correlacion);
        if (!UtilTexto.esVacioONulo(recortada)
                && !ValidatorLongitud.longitudMaxima(recortada, SeguridadLimits.Acceso.CORRELACION_MAX,
                SeguridadFields.Autorizacion.CORRELACION,
                SeguridadCodes.Autorizacion.ACCESO_CORRELACION_EXCEDE_LIMITE, result)) {
            return;
        }
        this.correlacion = recortada;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getCredencial() {
        return credencial;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getCorrelacion() {
        return correlacion;
    }
}
