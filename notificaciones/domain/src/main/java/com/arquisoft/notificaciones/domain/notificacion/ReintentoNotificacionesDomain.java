package com.arquisoft.notificaciones.domain.notificacion;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;

public final class ReintentoNotificacionesDomain {

    private static final int MINIMO = 1;

    private int maxIntentos;
    private int limite;

    private ReintentoNotificacionesDomain() {}

    public static ReintentoNotificacionesDomain crear(int maxIntentos, int limite) {
        var reintento = new ReintentoNotificacionesDomain();
        var result = new ValidationResult();

        reintento.setMaxIntentos(maxIntentos, result);
        reintento.setLimite(limite, result);

        result.lanzarSiTieneErrores();
        return reintento;
    }

    private void setMaxIntentos(int maxIntentos, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(maxIntentos, MINIMO,
                NotificacionesFields.Notificacion.MAX_INTENTOS,
                NotificacionesCodes.Notificacion.MAX_INTENTOS_INVALIDO, result)) {
            return;
        }
        this.maxIntentos = maxIntentos;
    }

    private void setLimite(int limite, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(limite, MINIMO,
                NotificacionesFields.Notificacion.LIMITE,
                NotificacionesCodes.Notificacion.LIMITE_INVALIDO, result)) {
            return;
        }
        this.limite = limite;
    }

    public int getMaxIntentos() {
        return maxIntentos;
    }

    public int getLimite() {
        return limite;
    }
}
