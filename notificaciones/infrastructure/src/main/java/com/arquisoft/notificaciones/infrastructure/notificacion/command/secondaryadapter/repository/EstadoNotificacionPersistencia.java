package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import lombok.Getter;

@Getter
public enum EstadoNotificacionPersistencia {

    PENDIENTE("PENDIENTE"),
    ENVIADA("ENVIADA"),
    FALLIDA("FALLIDA");

    private final String codigo;

    EstadoNotificacionPersistencia(String codigo) {
        this.codigo = codigo;
    }

}
