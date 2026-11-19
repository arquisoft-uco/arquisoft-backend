package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorConRegistrosFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorConRegistrosFinderImpl implements EvaluacionAsesorConRegistrosFinder {

    private final EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;

    @Override
    public Boolean obtener(UUID evaluacion) {
        return evaluacionAsesorOutputPort.existenRegistrosPorEvaluacion(evaluacion);
    }
}
