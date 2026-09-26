package com.arquisoft.fichas.application.estadorevision.query.usecase.impl;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.application.estadorevision.query.secondaryport.EstadoRevisionQueryOutputPort;
import com.arquisoft.fichas.application.estadorevision.query.usecase.ConsultarEstadosRevisionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoRevisionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRevisionUseCaseImpl implements ConsultarEstadosRevisionUseCase {

    private final EstadoRevisionQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoRevisionReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(EstadoRevisionKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
