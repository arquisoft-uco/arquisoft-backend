package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorPorObservacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper.ContextoEvaluacionCuantitativaAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoEvaluacionCuantitativaAsesorPorObservacionFinderImpl
        implements ContextoEvaluacionCuantitativaAsesorPorObservacionFinder {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;

    @Override
    public ContextoEvaluacionCuantitativaAsesorDomain obtener(UUID observacion) {
        return evaluacionCuantitativaAsesorOutputPort.obtenerContextoPorObservacion(observacion)
                .map(ContextoEvaluacionCuantitativaAsesorMapper::toDomain)
                .orElse(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
    }
}
