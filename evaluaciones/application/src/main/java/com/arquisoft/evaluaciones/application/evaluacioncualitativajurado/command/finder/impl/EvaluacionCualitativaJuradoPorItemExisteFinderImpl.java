package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionCualitativaJuradoPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaJuradoPorItemExisteFinderImpl
        implements EvaluacionCualitativaJuradoPorItemExisteFinder {

    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;

    @Override
    public Boolean obtener(UUID item) {
        return evaluacionCualitativaJuradoOutputPort.existePorItem(item);
    }
}
