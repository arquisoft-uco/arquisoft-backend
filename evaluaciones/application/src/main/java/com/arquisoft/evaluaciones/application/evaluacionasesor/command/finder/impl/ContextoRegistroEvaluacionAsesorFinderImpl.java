package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.ContextoRegistroEvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper.ContextoRegistroEvaluacionAsesorMapper;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContextoRegistroEvaluacionAsesorFinderImpl implements ContextoRegistroEvaluacionAsesorFinder {

    private final ContextoRegistroEvaluacionAsesorOutputPort contextoRegistroEvaluacionAsesorOutputPort;

    @Override
    public ContextoRegistroEvaluacionAsesorDomain obtener(UUID evaluacionAsesor) {
        return contextoRegistroEvaluacionAsesorOutputPort.obtenerContextoBloqueado(evaluacionAsesor)
                .map(ContextoRegistroEvaluacionAsesorMapper::toDomain)
                .orElse(ContextoRegistroEvaluacionAsesorDomain.VACIO);
    }
}
