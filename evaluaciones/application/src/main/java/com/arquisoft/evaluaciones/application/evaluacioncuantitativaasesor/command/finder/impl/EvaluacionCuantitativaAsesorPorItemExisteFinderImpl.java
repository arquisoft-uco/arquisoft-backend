package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.EvaluacionCuantitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaAsesorPorItemExisteFinderImpl
        implements EvaluacionCuantitativaAsesorPorItemExisteFinder {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;

    @Override
    public Boolean obtener(UUID item) {
        return evaluacionCuantitativaAsesorOutputPort.existePorItem(item);
    }
}
