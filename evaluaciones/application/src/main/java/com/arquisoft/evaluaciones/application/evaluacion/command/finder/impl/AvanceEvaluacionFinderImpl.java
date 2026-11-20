package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.AvanceEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.AvanceEvaluacionMapper;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AvanceEvaluacionFinderImpl implements AvanceEvaluacionFinder {

    private final EvaluacionOutputPort evaluacionOutputPort;

    @Override
    public AvanceEvaluacionDomain obtener(UUID evaluacion) {
        return evaluacionOutputPort.obtenerAvance(evaluacion)
                .map(AvanceEvaluacionMapper::toDomain)
                .orElse(AvanceEvaluacionDomain.VACIO);
    }
}
