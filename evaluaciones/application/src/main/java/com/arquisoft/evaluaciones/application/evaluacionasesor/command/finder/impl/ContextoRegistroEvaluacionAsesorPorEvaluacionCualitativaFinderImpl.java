package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.ContextoRegistroEvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper.ContextoRegistroEvaluacionAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinderImpl
        implements ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder {

    private final ContextoRegistroEvaluacionAsesorOutputPort contextoRegistroEvaluacionAsesorOutputPort;

    @Override
    public ContextoRegistroEvaluacionAsesorDomain obtener(UUID evaluacionCualitativa) {
        return contextoRegistroEvaluacionAsesorOutputPort
                .obtenerContextoBloqueadoPorEvaluacionCualitativa(evaluacionCualitativa)
                .map(ContextoRegistroEvaluacionAsesorMapper::toDomain)
                .orElse(ContextoRegistroEvaluacionAsesorDomain.VACIO);
    }
}
