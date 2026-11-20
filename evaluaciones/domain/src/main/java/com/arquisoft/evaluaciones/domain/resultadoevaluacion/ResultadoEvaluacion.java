package com.arquisoft.evaluaciones.domain.resultadoevaluacion;

import com.arquisoft.evaluaciones.domain.resultadoevaluacion.exception.ResultadoEvaluacionNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum ResultadoEvaluacion {

    APROBADA("Aprobada"),
    APROBADA_CON_OBSERVACIONES("Aprobada con observaciones"),
    RECHAZADA("Rechazada"),

    VACIO("");

    private final String nombre;

    ResultadoEvaluacion(String nombre) {
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

    public static ResultadoEvaluacion desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new ResultadoEvaluacionNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<ResultadoEvaluacion> delCatalogo(String id) {
        return UtilEnum.desde(ResultadoEvaluacion.class, id).filter(resultado -> resultado != VACIO);
    }
}
