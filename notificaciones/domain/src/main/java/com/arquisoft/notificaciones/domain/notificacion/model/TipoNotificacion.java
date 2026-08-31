package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.notificaciones.domain.notificacion.exception.TipoNotificacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum TipoNotificacion {

    ASESOR_FICHA_CAMBIADO,
    FICHA_PERFIL_REGISTRADA_ASESOR,
    ESTUDIANTES_FICHA_PERFIL_ASIGNADOS,

    VACIO;

    public String getId() {
        return name();
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public static TipoNotificacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new TipoNotificacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<TipoNotificacion> delCatalogo(String id) {
        return UtilEnum.desde(TipoNotificacion.class, id).filter(tipo -> tipo != VACIO);
    }
}
