package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class EstadoEvaluacionJuradoDomain {

    public static final EstadoEvaluacionJuradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO);

    private UUID id;
    private UUID jurado;
    private EstadoEvaluacion estado;

    private EstadoEvaluacionJuradoDomain() {}

    public static EstadoEvaluacionJuradoDomain reconstruir(UUID id, UUID jurado, EstadoEvaluacion estado) {
        var estadoEvaluacionJurado = new EstadoEvaluacionJuradoDomain();
        estadoEvaluacionJurado.id = id;
        estadoEvaluacionJurado.jurado = jurado;
        estadoEvaluacionJurado.estado = estado;
        return estadoEvaluacionJurado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getJurado() {
        return jurado;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }
}
