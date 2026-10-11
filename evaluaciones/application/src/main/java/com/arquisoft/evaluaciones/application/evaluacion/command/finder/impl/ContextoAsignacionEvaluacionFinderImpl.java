package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ContextoAsignacionEvaluacionMapper;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoAsignacionEvaluacionFinderImpl implements ContextoAsignacionEvaluacionFinder {

    private final EvaluacionOutputPort evaluacionOutputPort;

    @Override
    public ContextoAsignacionEvaluacionDomain obtener(UUID evaluacion) {
        return evaluacionOutputPort.obtenerContextoAsignacionBloqueado(evaluacion)
                .map(ContextoAsignacionEvaluacionMapper::toDomain)
                .orElse(ContextoAsignacionEvaluacionDomain.VACIO);
    }
}
