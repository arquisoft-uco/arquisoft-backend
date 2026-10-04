package com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionesDeEvaluacionesCuantitativasExistenFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesDeEvaluacionesCuantitativasExistenFinderImpl
        implements ObservacionesDeEvaluacionesCuantitativasExistenFinder {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;

    @Override
    public Boolean obtener(Set<UUID> evaluacionesCuantitativas) {
        return observacionItemJuradoOutputPort.existenPorEvaluacionesCuantitativas(evaluacionesCuantitativas);
    }
}
