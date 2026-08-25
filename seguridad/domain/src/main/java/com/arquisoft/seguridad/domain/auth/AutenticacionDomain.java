package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class AutenticacionDomain {

    private String correo;
    private String claveAcceso;

    private AutenticacionDomain() {}

    public static AutenticacionDomain crear(String correo, String claveAcceso) {
        var autenticacion = new AutenticacionDomain();
        var result = new ValidationResult();

        autenticacion.setCorreo(correo, result);
        autenticacion.setClaveAcceso(claveAcceso, result);

        result.lanzarSiTieneErrores();
        return autenticacion;
    }

    private void setCorreo(String correo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(correo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SeguridadFields.Autenticacion.EMAIL,
                SeguridadCodes.Autenticacion.EMAIL_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorTexto.correoValido(recortado,
                SeguridadFields.Autenticacion.EMAIL,
                SeguridadCodes.Autenticacion.EMAIL_FORMATO_INVALIDO, result)) {
            return;
        }
        this.correo = recortado;
    }

    private void setClaveAcceso(String claveAcceso, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(claveAcceso,
                SeguridadFields.Autenticacion.CONTRASENA,
                SeguridadCodes.Autenticacion.CONTRASENA_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(claveAcceso.length(), SeguridadLimits.Autenticacion.CONTRASENA_MIN,
                SeguridadFields.Autenticacion.CONTRASENA,
                SeguridadCodes.Autenticacion.CONTRASENA_DEMASIADO_CORTA, result)) {
            return;
        }
        this.claveAcceso = claveAcceso;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }
}
