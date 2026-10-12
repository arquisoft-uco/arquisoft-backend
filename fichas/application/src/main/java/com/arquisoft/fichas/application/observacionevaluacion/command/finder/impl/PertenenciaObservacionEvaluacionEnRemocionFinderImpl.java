package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionEnRemocionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.mapper.PertenenciaObservacionEvaluacionMapper;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PertenenciaObservacionEvaluacionEnRemocionFinderImpl
        implements PertenenciaObservacionEvaluacionEnRemocionFinder {

    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Override
    public PertenenciaObservacionEvaluacion obtener(RemocionObservacionEvaluacionDomain remocion) {
        return observacionEvaluacionOutputPort
                .obtenerPertenencia(remocion.getObservacionEvaluacion(), remocion.getRepresentanteComite())
                .map(PertenenciaObservacionEvaluacionMapper::toDomain)
                .orElse(PertenenciaObservacionEvaluacion.VACIO);
    }
}
