package com.arquisoft.mapas_ruta.domain.tarea.model;

import com.arquisoft.mapas_ruta.domain.tarea.exception.EstadoItemNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoItem {

    NUEVA("Nueva"),
    EN_PROGRESO("En progreso"),
    FINALIZADA("Finalizada"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoItem(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean puedePasarA(EstadoItem destino) {
        return switch (this) {
            case NUEVA -> destino == EN_PROGRESO || destino == FINALIZADA;
            case EN_PROGRESO -> destino == FINALIZADA;
            case FINALIZADA, VACIO -> false;
        };
    }

    public boolean permiteRemocion() {
        return this == NUEVA || this == EN_PROGRESO;
    }

    public static EstadoItem desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoItemNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoItem> delCatalogo(String id) {
        return UtilEnum.desde(EstadoItem.class, id).filter(estado -> estado != VACIO);
    }
}
