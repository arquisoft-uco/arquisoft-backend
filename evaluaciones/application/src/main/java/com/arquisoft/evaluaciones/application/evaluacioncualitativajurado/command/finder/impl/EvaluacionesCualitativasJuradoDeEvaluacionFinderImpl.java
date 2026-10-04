package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionesCualitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionesCualitativasJuradoDeEvaluacionFinderImpl
        implements EvaluacionesCualitativasJuradoDeEvaluacionFinder {

    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;

    @Override
    public Set<UUID> obtener(OmisionEvaluacionesCualitativasJuradoDomain omision) {
        return evaluacionCualitativaJuradoOutputPort.consultarIdsPorEvaluacionJurado(
                omision.getEvaluacionJurado(), omision.getEvaluaciones());
    }
}
