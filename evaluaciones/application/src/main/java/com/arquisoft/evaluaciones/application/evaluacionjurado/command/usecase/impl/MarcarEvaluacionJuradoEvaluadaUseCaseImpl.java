package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.MarcarEvaluacionJuradoEvaluadaUseCase;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarcarEvaluacionJuradoEvaluadaUseCaseImpl implements MarcarEvaluacionJuradoEvaluadaUseCase {

    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CompletitudEvaluacionJuradoDomain completitud) {
        if (completitud.requiereTransicion()) {
            evaluacionJuradoOutputPort.actualizarEstado(
                    completitud.getEvaluacionJurado(), completitud.estadoDestino().getId());
            logger.debug(EvaluacionJuradoKey.LOG_EVALUADA,
                    completitud.getEvaluacionJurado(), completitud.estadoDestino().getId());
        }
    }
}
