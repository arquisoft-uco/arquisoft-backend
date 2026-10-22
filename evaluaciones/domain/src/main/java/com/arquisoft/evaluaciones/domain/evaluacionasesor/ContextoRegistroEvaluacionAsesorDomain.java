package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ContextoRegistroEvaluacionAsesorDomain {

    public static final ContextoRegistroEvaluacionAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto());

    private UUID id;
    private UUID evaluacion;
    private EstadoEvaluacion estado;
    private UUID entregable;
    private UUID asesor;

    private ContextoRegistroEvaluacionAsesorDomain() {}

    public static ContextoRegistroEvaluacionAsesorDomain reconstruir(
            UUID id, UUID evaluacion, EstadoEvaluacion estado, UUID entregable, UUID asesor) {
        var contexto = new ContextoRegistroEvaluacionAsesorDomain();
        contexto.id = id;
        contexto.evaluacion = evaluacion;
        contexto.estado = estado;
        contexto.entregable = entregable;
        contexto.asesor = asesor;
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

    public UUID getAsesor() {
        return asesor;
    }
}
