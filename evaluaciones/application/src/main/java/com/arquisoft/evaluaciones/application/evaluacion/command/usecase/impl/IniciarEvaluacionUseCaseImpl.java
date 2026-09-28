package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.IniciarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarEvaluacionUseCaseImpl implements IniciarEvaluacionUseCase {

    private final EvaluacionOutputPort evaluacionOutputPort;
    private final IniciarEvaluacionValidator iniciarEvaluacionValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(InicioEvaluacionDomain inicio) {
        iniciarEvaluacionValidator.validar(inicio.getEstadoActual());

        if (inicio.requiereTransicion()) {
            evaluacionOutputPort.actualizarEstado(inicio.getEvaluacion(), inicio.estadoDestino().getId());
            logger.debug(EvaluacionKey.LOG_INICIADA,
                    inicio.getEvaluacion(), inicio.getEstadoActual().getId(), inicio.estadoDestino().getId());
        }
    }
}
