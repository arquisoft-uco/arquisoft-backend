package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.mapper.EvaluacionCualitativaAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaAsesorPorIdFinderImpl implements EvaluacionCualitativaAsesorPorIdFinder {

    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;

    @Override
    public EvaluacionCualitativaAsesorDomain obtener(UUID id) {
        return evaluacionCualitativaAsesorOutputPort.obtenerPorId(id)
                .map(EvaluacionCualitativaAsesorMapper::toDomain)
                .orElse(EvaluacionCualitativaAsesorDomain.VACIO);
    }
}
