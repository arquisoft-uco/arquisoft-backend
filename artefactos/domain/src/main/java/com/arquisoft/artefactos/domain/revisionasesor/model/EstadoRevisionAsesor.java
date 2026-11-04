package com.arquisoft.artefactos.domain.revisionasesor.model;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoRevisionAsesorNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoRevisionAsesor {

    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    RESUELTA("Resuelta"),
    CERRADA("Cerrada"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoRevisionAsesor(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esResuelta() {
        return this == RESUELTA;
    }

    public static EstadoRevisionAsesor desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoRevisionAsesorNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoRevisionAsesor> delCatalogo(String id) {
        return UtilEnum.desde(EstadoRevisionAsesor.class, id).filter(estado -> estado != VACIO);
    }
}
