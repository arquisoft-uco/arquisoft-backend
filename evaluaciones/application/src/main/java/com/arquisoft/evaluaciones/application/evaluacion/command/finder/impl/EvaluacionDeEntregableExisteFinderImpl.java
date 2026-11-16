package com.arquisoft.evaluaciones.application.evaluacion.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.EvaluacionDeEntregableExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionDeEntregableExisteFinderImpl implements EvaluacionDeEntregableExisteFinder {

    private final EvaluacionOutputPort evaluacionOutputPort;

    @Override
    public Boolean obtener(UUID entregable) {
        return evaluacionOutputPort.existePorEntregable(entregable);
    }
}
