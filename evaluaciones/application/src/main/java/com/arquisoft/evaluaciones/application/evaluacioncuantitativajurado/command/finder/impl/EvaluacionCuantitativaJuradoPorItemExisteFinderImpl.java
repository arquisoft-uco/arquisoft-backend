package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoPorItemExisteFinderImpl
        implements EvaluacionCuantitativaJuradoPorItemExisteFinder {

    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;

    @Override
    public Boolean obtener(UUID item) {
        return evaluacionCuantitativaJuradoOutputPort.existePorItem(item);
    }
}
