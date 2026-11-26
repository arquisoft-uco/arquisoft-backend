package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record EvaluacionExistente(UUID evaluacion, EstadoEvaluacion estadoActual) {

    public static final EvaluacionExistente VACIO =
            new EvaluacionExistente(UtilUUID.obtenerUUIDPorDefecto(), EstadoEvaluacion.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
