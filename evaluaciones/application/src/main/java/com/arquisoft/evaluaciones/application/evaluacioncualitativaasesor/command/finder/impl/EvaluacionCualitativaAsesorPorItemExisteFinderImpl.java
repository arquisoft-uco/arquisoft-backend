package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaAsesorPorItemExisteFinderImpl
        implements EvaluacionCualitativaAsesorPorItemExisteFinder {

    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;

    @Override
    public Boolean obtener(UUID item) {
        return evaluacionCualitativaAsesorOutputPort.existePorItem(item);
    }
}
