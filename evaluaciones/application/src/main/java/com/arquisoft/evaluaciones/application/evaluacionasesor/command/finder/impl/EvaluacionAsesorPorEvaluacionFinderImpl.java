package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorPorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper.EvaluacionAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorPorEvaluacionFinderImpl implements EvaluacionAsesorPorEvaluacionFinder {

    private final EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;

    @Override
    public EvaluacionAsesorDomain obtener(UUID evaluacion) {
        return evaluacionAsesorOutputPort.obtenerPorEvaluacion(evaluacion)
                .map(EvaluacionAsesorMapper::toDomain)
                .orElse(EvaluacionAsesorDomain.VACIO);
    }
}
