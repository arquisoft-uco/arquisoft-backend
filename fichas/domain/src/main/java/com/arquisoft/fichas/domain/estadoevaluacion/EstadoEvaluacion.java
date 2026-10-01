package com.arquisoft.fichas.domain.estadoevaluacion;

import com.arquisoft.fichas.domain.estadoevaluacion.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoEvaluacion {

    EN_EVALUACION("En Evaluación"),
    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada Con Observaciones"),
    NO_APROBADA("No Aprobada"),
    DESCARTADA("Descartada"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoEvaluacion(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esTerminal() {
        return this == APROBADA || this == NO_APROBADA || this == DESCARTADA || this == APROBADA_CON_OBSERVACIONES;
    }

    public boolean esEnEvaluacion() {
        return this == EN_EVALUACION;
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
