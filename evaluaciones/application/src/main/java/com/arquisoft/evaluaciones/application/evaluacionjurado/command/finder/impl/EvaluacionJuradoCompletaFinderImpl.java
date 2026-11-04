package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoCompletaFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoCompletaFinderImpl implements EvaluacionJuradoCompletaFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public Boolean obtener(UUID evaluacionJurado) {
        return outputPort.estaCompleta(evaluacionJurado);
    }
}
