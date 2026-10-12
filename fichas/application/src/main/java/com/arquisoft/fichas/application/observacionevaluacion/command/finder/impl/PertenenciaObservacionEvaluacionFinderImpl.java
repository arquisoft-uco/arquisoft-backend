package com.arquisoft.fichas.application.observacionevaluacion.command.finder.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.mapper.PertenenciaObservacionEvaluacionMapper;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PertenenciaObservacionEvaluacionFinderImpl implements PertenenciaObservacionEvaluacionFinder {

    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Override
    public PertenenciaObservacionEvaluacion obtener(ModificacionObservacionEvaluacionDomain modificacion) {
        return observacionEvaluacionOutputPort
                .obtenerPertenencia(modificacion.getObservacionEvaluacion(), modificacion.getRepresentanteComite())
                .map(PertenenciaObservacionEvaluacionMapper::toDomain)
                .orElse(PertenenciaObservacionEvaluacion.VACIO);
    }
}
