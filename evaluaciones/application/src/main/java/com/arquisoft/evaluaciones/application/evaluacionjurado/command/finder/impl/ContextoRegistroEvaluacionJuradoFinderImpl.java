package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.ContextoRegistroEvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper.ContextoRegistroEvaluacionJuradoMapper;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.ContextoRegistroEvaluacionJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionJuradoFinderImpl implements ContextoRegistroEvaluacionJuradoFinder {

    private final ContextoRegistroEvaluacionJuradoOutputPort evaluacionJuradoOutputPort;

    @Override
    public ContextoRegistroEvaluacionJuradoDomain obtener(UUID evaluacionJurado) {
        return evaluacionJuradoOutputPort.obtenerContextoBloqueado(evaluacionJurado)
                .map(ContextoRegistroEvaluacionJuradoMapper::toDomain)
                .orElse(ContextoRegistroEvaluacionJuradoDomain.VACIO);
    }
}
