package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCommandOutputAdapter implements EvaluacionOutputPort {

    private final EvaluacionCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void actualizarEstado(UUID evaluacion, String estado) {
        repository.actualizarEstado(evaluacion, estado);
        logger.debug(EvaluacionKey.LOG_ESTADO_ACTUALIZADO, evaluacion, estado);
    }
}
