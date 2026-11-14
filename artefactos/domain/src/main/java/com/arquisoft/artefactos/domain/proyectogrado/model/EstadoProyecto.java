package com.arquisoft.artefactos.domain.proyectogrado.model;

import com.arquisoft.artefactos.domain.proyectogrado.exception.EstadoProyectoNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoProyecto {

    EN_PROCESO("En proceso"),
    ATRASADO("Atrasado"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoProyecto(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoProyecto desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoProyectoNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoProyecto> delCatalogo(String id) {
        return UtilEnum.desde(EstadoProyecto.class, id).filter(estado -> estado != VACIO);
    }
}
