package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper.EstadoEvaluacionJuradoMapper;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoEstadoFinderImpl implements EvaluacionJuradoEstadoFinder {

    private final EvaluacionJuradoOutputPort outputPort;

    @Override
    public EstadoEvaluacionJuradoDomain obtener(UUID evaluacionJurado) {
        return outputPort.obtenerEstadoBloqueado(evaluacionJurado)
                .map(EstadoEvaluacionJuradoMapper::toDomain)
                .orElse(EstadoEvaluacionJuradoDomain.VACIO);
    }
}
