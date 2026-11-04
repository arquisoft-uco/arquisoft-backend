package com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorDeEvaluacionAsesorQueryFinderImpl implements AsesorDeEvaluacionAsesorQueryFinder {

    private final EvaluacionAsesorAccesoQueryOutputPort evaluacionAsesorAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID evaluacionAsesor) {
        return evaluacionAsesorAccesoQueryOutputPort.obtenerAsesorPorId(evaluacionAsesor)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
