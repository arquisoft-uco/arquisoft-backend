package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ResumenEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ResumenesEvaluacionJuradosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumenesEvaluacionJuradosFinderImpl implements ResumenesEvaluacionJuradosFinder {

    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;

    @Override
    public List<ResumenEvaluacionDomain> obtener(UUID evaluacion) {
        return evaluacionJuradoOutputPort.consultarResumenesPorEvaluacion(evaluacion).stream()
                .map(ResumenEvaluacionMapper::toDomain)
                .toList();
    }
}
