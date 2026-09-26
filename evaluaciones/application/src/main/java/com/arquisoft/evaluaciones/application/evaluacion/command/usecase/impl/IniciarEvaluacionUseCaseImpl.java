package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.IniciarEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarEvaluacionUseCaseImpl implements IniciarEvaluacionUseCase {

    private final EvaluacionOutputPort evaluacionOutputPort;
    private final IniciarEvaluacionValidator iniciarEvaluacionValidator;

    @Override
    public void ejecutar(InicioEvaluacionDomain inicio) {
        iniciarEvaluacionValidator.validar(inicio.getEstadoActual());

        if (inicio.getEstadoActual() == EstadoEvaluacion.PENDIENTE) {
            evaluacionOutputPort.actualizarEstado(inicio.getEvaluacion(), inicio.estadoDestino().getId());
        }
    }
}
