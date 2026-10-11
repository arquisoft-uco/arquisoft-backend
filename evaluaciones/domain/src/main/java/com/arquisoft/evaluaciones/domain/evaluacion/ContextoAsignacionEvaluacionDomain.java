package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ContextoAsignacionEvaluacionDomain {

    public static final ContextoAsignacionEvaluacionDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO);

    private UUID evaluacion;
    private EstadoEvaluacion estado;

    private ContextoAsignacionEvaluacionDomain() {}

    public static ContextoAsignacionEvaluacionDomain reconstruir(UUID evaluacion, EstadoEvaluacion estado) {
        var contexto = new ContextoAsignacionEvaluacionDomain();
        contexto.evaluacion = evaluacion;
        contexto.estado = estado;
        return contexto;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }
}
