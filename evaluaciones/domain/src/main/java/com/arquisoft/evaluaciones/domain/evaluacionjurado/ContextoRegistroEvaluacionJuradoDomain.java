package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ContextoRegistroEvaluacionJuradoDomain {

    public static final ContextoRegistroEvaluacionJuradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO,
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private UUID evaluacion;
    private EstadoEvaluacion estado;
    private UUID entregable;

    private ContextoRegistroEvaluacionJuradoDomain() {}

    public static ContextoRegistroEvaluacionJuradoDomain reconstruir(
            UUID id, UUID evaluacion, EstadoEvaluacion estado, UUID entregable) {
        var contexto = new ContextoRegistroEvaluacionJuradoDomain();
        contexto.id = id;
        contexto.evaluacion = evaluacion;
        contexto.estado = estado;
        contexto.entregable = entregable;
        return contexto;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public UUID getEntregable() {
        return entregable;
    }
}
