package com.arquisoft.proyectos.domain.estadoasesor;

import com.arquisoft.proyectos.domain.estadoasesor.exception.EstadoAsesorNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoAsesor {

    ASIGNADO("Asignado"),
    FINALIZADO("Finalizado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoAsesor(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoAsesor desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoAsesorNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoAsesor> delCatalogo(String id) {
        return UtilEnum.desde(EstadoAsesor.class, id).filter(estado -> estado != VACIO);
    }
}
