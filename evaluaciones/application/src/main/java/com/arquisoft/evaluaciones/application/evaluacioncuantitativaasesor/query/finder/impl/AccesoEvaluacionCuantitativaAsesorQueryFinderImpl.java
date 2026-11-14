package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.AccesoEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorAccesoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.mapper.EvaluacionCuantitativaAsesorAccesoMapper;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccesoEvaluacionCuantitativaAsesorQueryFinderImpl implements AccesoEvaluacionCuantitativaAsesorQueryFinder {

    private final EvaluacionCuantitativaAsesorAccesoQueryOutputPort evaluacionCuantitativaAsesorAccesoQueryOutputPort;

    @Override
    public AccesoEvaluacionCuantitativaAsesorDomain obtener(UUID evaluacionCuantitativaAsesor) {
        return evaluacionCuantitativaAsesorAccesoQueryOutputPort.obtenerAccesoPorId(evaluacionCuantitativaAsesor)
                .map(EvaluacionCuantitativaAsesorAccesoMapper::toDomain)
                .orElse(AccesoEvaluacionCuantitativaAsesorDomain.VACIO);
    }
}
