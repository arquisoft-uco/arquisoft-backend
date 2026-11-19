package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.ConsolidacionNotasEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.ConsolidarNotasEvaluacionUseCase;
import com.arquisoft.evaluaciones.domain.evaluacion.ConsolidacionNotasEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsolidarNotasEvaluacionUseCaseImpl implements ConsolidarNotasEvaluacionUseCase {

    private final EvaluacionOutputPort evaluacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ConsolidacionNotasEvaluacionDomain consolidacion) {
        evaluacionOutputPort.actualizarNotas(ConsolidacionNotasEvaluacionMapper.toEntity(consolidacion));

        logger.debug(
                EvaluacionKey.LOG_NOTAS_CONSOLIDADAS,
                consolidacion.getEvaluacion(),
                consolidacion.getNotaAsesor(),
                consolidacion.getNotaJurados(),
                consolidacion.getCantidadJurados());
    }
}
