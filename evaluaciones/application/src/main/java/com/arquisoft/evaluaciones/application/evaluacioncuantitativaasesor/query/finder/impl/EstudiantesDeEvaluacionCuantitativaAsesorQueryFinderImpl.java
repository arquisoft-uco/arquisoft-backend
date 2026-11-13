package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesDeEvaluacionCuantitativaAsesorQueryFinderImpl
        implements EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder {

    private final EvaluacionCuantitativaAsesorAccesoQueryOutputPort evaluacionCuantitativaAsesorAccesoQueryOutputPort;

    @Override
    public List<UUID> obtener(UUID evaluacionCuantitativaAsesor) {
        return evaluacionCuantitativaAsesorAccesoQueryOutputPort
                .obtenerEstudiantesVigentesPorId(evaluacionCuantitativaAsesor);
    }
}
