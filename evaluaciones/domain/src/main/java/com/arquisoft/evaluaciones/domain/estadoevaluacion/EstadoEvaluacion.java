package com.arquisoft.evaluaciones.domain.estadoevaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoEvaluacion {

    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    FINALIZADA("Finalizada"),

    VACIO("");

    private final String nombre;

    EstadoEvaluacion(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return name();
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public static EstadoEvaluacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoEvaluacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoEvaluacion> delCatalogo(String id) {
        return UtilEnum.desde(EstadoEvaluacion.class, id).filter(estado -> estado != VACIO);
    }
}
