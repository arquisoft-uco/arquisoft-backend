package com.arquisoft.notificaciones.application.notificacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;

public record ReintentarNotificacionesFallidasCommand(int maxIntentos, int limite) {

    public static ReintentarNotificacionesFallidasCommand crear(int maxIntentos, int limite) {
        var result = new ValidationResult();

        ValidatorNumero.valorMinimo(maxIntentos, 1,
                NotificacionesFields.Notificacion.MAX_INTENTOS,
                NotificacionesCodes.Notificacion.MAX_INTENTOS_INVALIDO, result);
        ValidatorNumero.valorMinimo(limite, 1,
                NotificacionesFields.Notificacion.LIMITE,
                NotificacionesCodes.Notificacion.LIMITE_INVALIDO, result);

        result.lanzarSiTieneErroresDeEntrada();
        return new ReintentarNotificacionesFallidasCommand(maxIntentos, limite);
    }
}
