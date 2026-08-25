package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class SesionDomain {

    private String identificadorToken;
    private long tiempoVidaRestante;

    private SesionDomain() {}

    public static SesionDomain crear(String identificadorToken, long tiempoVidaRestante) {
        var sesion = new SesionDomain();
        var result = new ValidationResult();

        sesion.setIdentificadorToken(identificadorToken, result);
        sesion.setTiempoVidaRestante(tiempoVidaRestante, result);

        result.lanzarSiTieneErrores();
        return sesion;
    }

    private void setIdentificadorToken(String identificadorToken, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificadorToken,
                SeguridadFields.Sesion.IDENTIFICADOR_TOKEN,
                SeguridadCodes.Sesion.SESION_IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificadorToken = UtilTexto.aplicarTrim(identificadorToken);
    }

    private void setTiempoVidaRestante(long tiempoVidaRestante, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(tiempoVidaRestante, SeguridadLimits.Sesion.TIEMPO_VIDA_MIN,
                SeguridadFields.Sesion.TIEMPO_VIDA_RESTANTE,
                SeguridadCodes.Sesion.SESION_TTL_INVALIDO, result)) {
            return;
        }
        this.tiempoVidaRestante = tiempoVidaRestante;
    }

    public boolean requiereInvalidacion() {
        return tiempoVidaRestante > SeguridadLimits.Sesion.TIEMPO_VIDA_MIN;
    }

    public String getIdentificadorToken() {
        return identificadorToken;
    }

    public long getTiempoVidaRestante() {
        return tiempoVidaRestante;
    }
}
