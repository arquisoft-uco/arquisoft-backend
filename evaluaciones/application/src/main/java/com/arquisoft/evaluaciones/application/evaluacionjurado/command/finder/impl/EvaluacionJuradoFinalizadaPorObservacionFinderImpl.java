package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaPorObservacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoFinalizadaPorObservacionFinderImpl
        implements EvaluacionJuradoFinalizadaPorObservacionFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public Boolean obtener(UUID observacion) {
        return outputPort.estaFinalizadaPorObservacion(observacion);
    }
}
