package com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EstudiantesDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudiantesDeEvaluacionAsesorQueryFinderImpl implements EstudiantesDeEvaluacionAsesorQueryFinder {

    private final EvaluacionAsesorAccesoQueryOutputPort evaluacionAsesorAccesoQueryOutputPort;

    @Override
    public List<UUID> obtener(UUID evaluacionAsesor) {
        return evaluacionAsesorAccesoQueryOutputPort.obtenerEstudiantesVigentesPorId(evaluacionAsesor);
    }
}
