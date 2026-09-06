package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp;

import lombok.Getter;

@Getter
public enum TipoNotificacionEvento {

    ASESOR_FICHA_CAMBIADO("ASESOR_FICHA_CAMBIADO"),
    FICHA_PERFIL_REGISTRADA_ASESOR("FICHA_PERFIL_REGISTRADA_ASESOR"),
    ESTUDIANTES_FICHA_PERFIL_ASIGNADOS("ESTUDIANTES_FICHA_PERFIL_ASIGNADOS"),
    REVISION_ITEM_AGREGADO("REVISION_ITEM_AGREGADO");

    private final String codigo;

    TipoNotificacionEvento(String codigo) {
        this.codigo = codigo;
    }

}
