package com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionEvaluacionesUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstadoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEvaluacionEvaluacionesUseCaseImpl implements ConsultarEstadosEvaluacionEvaluacionesUseCase {

    private final EstadoEvaluacionQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoEvaluacionReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(EstadoEvaluacionKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
