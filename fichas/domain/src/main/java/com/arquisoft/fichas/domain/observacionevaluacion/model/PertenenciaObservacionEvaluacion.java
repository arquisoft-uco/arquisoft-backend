package com.arquisoft.fichas.domain.observacionevaluacion.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record PertenenciaObservacionEvaluacion(UUID evaluacionFichaPerfil, boolean esPropietario,
                                               EstadoEvaluacion ultimoEstado) {

    public static final PertenenciaObservacionEvaluacion VACIO = new PertenenciaObservacionEvaluacion(
            UtilUUID.obtenerUUIDPorDefecto(), false, EstadoEvaluacion.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
