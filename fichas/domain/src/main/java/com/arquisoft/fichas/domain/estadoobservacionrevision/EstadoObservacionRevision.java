package com.arquisoft.fichas.domain.estadoobservacionrevision;

import com.arquisoft.fichas.domain.estadoobservacionrevision.exception.EstadoObservacionRevisionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoObservacionRevision {

    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Progreso"),
    CERRADO("Cerrado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoObservacionRevision(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoObservacionRevision desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoObservacionRevisionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoObservacionRevision> delCatalogo(String id) {
        return UtilEnum.desde(EstadoObservacionRevision.class, id).filter(estado -> estado != VACIO);
    }
}
