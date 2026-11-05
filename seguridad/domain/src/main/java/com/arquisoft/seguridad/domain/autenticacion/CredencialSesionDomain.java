package com.arquisoft.seguridad.domain.autenticacion;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class CredencialSesionDomain {

    private String credencial;

    private CredencialSesionDomain() {}

    public static CredencialSesionDomain crear(String credencial) {
        var sesion = new CredencialSesionDomain();
        var result = new ValidationResult();

        sesion.setCredencial(credencial, result);

        result.lanzarSiTieneErrores();
        return sesion;
    }

    private void setCredencial(String credencial, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(credencial);
        if (!ValidatorTexto.noEnBlanco(recortada,
                SeguridadFields.CredencialSesion.CREDENCIAL,
                SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, SeguridadLimits.CredencialSesion.CREDENCIAL_MAX,
                SeguridadFields.CredencialSesion.CREDENCIAL,
                SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_EXCEDE_LIMITE, result)) {
            return;
        }
        this.credencial = recortada;
    }

    public String getCredencial() {
        return credencial;
    }
}
