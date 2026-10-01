package com.arquisoft.fichas.application.estadoevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEvaluacionUseCaseImpl implements ConsultarEstadosEvaluacionUseCase {

    private final EstadoEvaluacionQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoEvaluacionReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(EstadoEvaluacionKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
