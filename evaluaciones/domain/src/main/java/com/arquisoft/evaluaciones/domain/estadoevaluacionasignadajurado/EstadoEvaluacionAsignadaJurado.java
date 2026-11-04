package com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado.exception.EstadoEvaluacionAsignadaJuradoNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoEvaluacionAsignadaJurado {

    PENDIENTE_POR_EVALUAR("Pendiente por evaluar"),
    EVALUADA("Evaluada"),

    VACIO("");

    private final String nombre;

    EstadoEvaluacionAsignadaJurado(String nombre) {
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

    public static EstadoEvaluacionAsignadaJurado desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoEvaluacionAsignadaJuradoNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoEvaluacionAsignadaJurado> delCatalogo(String id) {
        return UtilEnum.desde(EstadoEvaluacionAsignadaJurado.class, id).filter(estado -> estado != VACIO);
    }
}
