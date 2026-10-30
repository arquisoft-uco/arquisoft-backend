package com.arquisoft.artefactos.domain.observacion.model;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoObservacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoObservacion {

    NUEVA("Nueva"),
    EN_PROGRESO("En progreso"),
    CERRADA("Cerrada"),
    SOLUCIONADA("Solucionada"),
    REABIERTA("Reabierta"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoObservacion(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoObservacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoObservacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoObservacion> delCatalogo(String id) {
        return UtilEnum.desde(EstadoObservacion.class, id).filter(estado -> estado != VACIO);
    }
}
