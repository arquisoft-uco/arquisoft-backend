package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorCompletaFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorCompletaFinderImpl implements EvaluacionAsesorCompletaFinder {

    private final EvaluacionAsesorOutputPort outputPort;

    @Override
    public Boolean obtener(UUID evaluacionAsesor) {
        return outputPort.estaCompleta(evaluacionAsesor);
    }
}
