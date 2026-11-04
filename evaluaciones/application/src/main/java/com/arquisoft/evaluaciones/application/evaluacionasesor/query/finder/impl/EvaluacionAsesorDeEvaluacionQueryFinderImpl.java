package com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EvaluacionAsesorDeEvaluacionQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorDeEvaluacionQueryFinderImpl implements EvaluacionAsesorDeEvaluacionQueryFinder {

    private final EvaluacionAsesorAccesoQueryOutputPort evaluacionAsesorAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID evaluacion) {
        return evaluacionAsesorAccesoQueryOutputPort.obtenerPorEvaluacion(evaluacion)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
