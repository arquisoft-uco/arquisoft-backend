package com.arquisoft.evaluaciones.domain.evaluacion.model;

import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public record ResumenesEvaluacion(
        UUID evaluacion,
        ResumenEvaluacionDomain asesor,
        List<ResumenEvaluacionDomain> jurados,
        long juradosEvaluados) {

    public ResumenesEvaluacion {
        jurados = UtilColeccion.aplicarPorDefecto(jurados);
    }
}
