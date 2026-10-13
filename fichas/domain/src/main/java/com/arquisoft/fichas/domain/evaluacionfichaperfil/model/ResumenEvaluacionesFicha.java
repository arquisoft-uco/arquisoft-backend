package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public record ResumenEvaluacionesFicha(UUID fichaPerfil, List<ConteoEvaluacionesPorEstado> conteos) {

    public ResumenEvaluacionesFicha {
        conteos = UtilColeccion.aplicarPorDefecto(conteos);
    }

    public long finalizadas() {
        return conteos.stream()
                .filter(conteo -> conteo.estado().esFinalizada())
                .mapToLong(ConteoEvaluacionesPorEstado::evaluaciones)
                .sum();
    }

    public long aprobatorias() {
        return conteos.stream()
                .filter(conteo -> conteo.estado().esAprobatoria())
                .mapToLong(ConteoEvaluacionesPorEstado::evaluaciones)
                .sum();
    }

    public long enEvaluacion() {
        return conteos.stream()
                .filter(conteo -> conteo.estado().esEnEvaluacion())
                .mapToLong(ConteoEvaluacionesPorEstado::evaluaciones)
                .sum();
    }

    public boolean tieneObservacionesVigentes() {
        return conteos.stream()
                .filter(conteo -> !conteo.estado().esDescartada())
                .mapToLong(ConteoEvaluacionesPorEstado::evaluacionesConObservaciones)
                .sum() > 0;
    }
}
