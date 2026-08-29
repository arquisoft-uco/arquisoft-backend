package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp;

import lombok.Getter;

@Getter
public enum TipoNotificacionEvento {

    ASESOR_FICHA_CAMBIADO("ASESOR_FICHA_CAMBIADO");

    private final String codigo;

    TipoNotificacionEvento(String codigo) {
        this.codigo = codigo;
    }

}
