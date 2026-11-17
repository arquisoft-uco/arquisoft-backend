package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.mapper.ContextoEvaluacionCuantitativaAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoEvaluacionCuantitativaAsesorFinderImpl implements ContextoEvaluacionCuantitativaAsesorFinder {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;

    @Override
    public ContextoEvaluacionCuantitativaAsesorDomain obtener(UUID evaluacionCuantitativaAsesor) {
        return evaluacionCuantitativaAsesorOutputPort.obtenerContexto(evaluacionCuantitativaAsesor)
                .map(ContextoEvaluacionCuantitativaAsesorMapper::toDomain)
                .orElse(ContextoEvaluacionCuantitativaAsesorDomain.VACIO);
    }
}
