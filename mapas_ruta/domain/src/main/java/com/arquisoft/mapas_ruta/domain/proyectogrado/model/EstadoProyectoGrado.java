package com.arquisoft.mapas_ruta.domain.proyectogrado.model;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoProyectoGrado {

    EN_PROCESO("En proceso"),
    LISTO_PARA_REVISION("Listo Para Revisión"),
    ATRASADO("Atrasado"),
    FINALIZADO("Finalizado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoProyectoGrado(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean estaEnProceso() {
        return this == EN_PROCESO;
    }

    public static EstadoProyectoGrado desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoProyectoGradoNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoProyectoGrado> delCatalogo(String id) {
        return UtilEnum.desde(EstadoProyectoGrado.class, id).filter(estado -> estado != VACIO);
    }
}
