package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ResumenEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumenEvaluacionAsesorFinderImpl implements ResumenEvaluacionAsesorFinder {

    private final EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;

    @Override
    public ResumenEvaluacionDomain obtener(UUID evaluacion) {
        return evaluacionAsesorOutputPort.obtenerResumenPorEvaluacion(evaluacion)
                .map(ResumenEvaluacionMapper::toDomain)
                .orElse(ResumenEvaluacionDomain.VACIO);
    }
}
