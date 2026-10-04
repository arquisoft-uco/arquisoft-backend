package com.arquisoft.fichas.domain.estadorevision;

import com.arquisoft.fichas.domain.estadorevision.exception.EstadoRevisionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoRevision {

    NUEVA("Nueva"),
    VISUALIZADA("Visualizada"),
    EN_PROGRESO("En Progreso"),
    CORRECCION_DISPONIBLE("Correccion Disponible"),
    CERRADA("Cerrada"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoRevision(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoRevision desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoRevisionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoRevision> delCatalogo(String id) {
        return UtilEnum.desde(EstadoRevision.class, id).filter(estado -> estado != VACIO);
    }
}
