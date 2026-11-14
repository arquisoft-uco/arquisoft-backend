package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ContextoEvaluacionCuantitativaAsesorDomain {

    public static final ContextoEvaluacionCuantitativaAsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO);

    private UUID id;
    private UUID evaluacionAsesor;
    private UUID asesor;
    private EstadoEvaluacion estado;

    private ContextoEvaluacionCuantitativaAsesorDomain() {}

    public static ContextoEvaluacionCuantitativaAsesorDomain reconstruir(
            UUID id, UUID evaluacionAsesor, UUID asesor, EstadoEvaluacion estado) {
        var contexto = new ContextoEvaluacionCuantitativaAsesorDomain();
        contexto.id = id;
        contexto.evaluacionAsesor = evaluacionAsesor;
        contexto.asesor = asesor;
        contexto.estado = estado;
        return contexto;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }
}
