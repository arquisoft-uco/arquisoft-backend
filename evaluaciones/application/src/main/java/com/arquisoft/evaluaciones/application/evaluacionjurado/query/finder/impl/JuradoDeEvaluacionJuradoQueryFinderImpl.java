package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.JuradoDeEvaluacionJuradoQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoDeEvaluacionJuradoQueryFinderImpl implements JuradoDeEvaluacionJuradoQueryFinder {

    private final EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID evaluacionJurado) {
        return evaluacionJuradoAccesoQueryOutputPort.obtenerJuradoPorId(evaluacionJurado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
