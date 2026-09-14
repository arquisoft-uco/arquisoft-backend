package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.ContextoRegistroEvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionJuradoFinderImpl implements ContextoRegistroEvaluacionJuradoFinder {

    private final ContextoRegistroEvaluacionJuradoOutputPort evaluacionJuradoOutputPort;

    @Override
    public Optional<ContextoRegistroEvaluacionJuradoEntity> obtener(UUID evaluacionJurado) {
        return evaluacionJuradoOutputPort.obtenerContextoBloqueado(evaluacionJurado);
    }
}
