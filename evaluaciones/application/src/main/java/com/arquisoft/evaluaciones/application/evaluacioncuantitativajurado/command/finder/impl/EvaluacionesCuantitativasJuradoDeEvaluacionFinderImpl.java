package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionesCuantitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionesCuantitativasJuradoDeEvaluacionFinderImpl
        implements EvaluacionesCuantitativasJuradoDeEvaluacionFinder {

    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;

    @Override
    public Set<UUID> obtener(OmisionEvaluacionesCuantitativasJuradoDomain omision) {
        return evaluacionCuantitativaJuradoOutputPort.consultarIdsPorEvaluacionJurado(
                omision.getEvaluacionJurado(), omision.getEvaluaciones());
    }
}
