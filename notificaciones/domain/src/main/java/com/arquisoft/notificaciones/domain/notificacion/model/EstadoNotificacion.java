package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.EstadoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoNotificacion {

    PENDIENTE,
    ENVIADA,
    FALLIDA,

    VACIO;

    public String getId() {
        return name();
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public boolean esTerminal() {
        return this == ENVIADA || this == FALLIDA;
    }

    public static EstadoNotificacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoNotificacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoNotificacion> delCatalogo(String id) {
        return UtilEnum.desde(EstadoNotificacion.class, id).filter(estado -> estado != VACIO);
    }
}
