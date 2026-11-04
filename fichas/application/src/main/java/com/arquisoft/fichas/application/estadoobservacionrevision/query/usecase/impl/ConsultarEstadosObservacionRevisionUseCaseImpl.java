package com.arquisoft.fichas.application.estadoobservacionrevision.query.usecase.impl;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.secondaryport.EstadoObservacionRevisionQueryOutputPort;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.usecase.ConsultarEstadosObservacionRevisionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoObservacionRevisionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosObservacionRevisionUseCaseImpl implements ConsultarEstadosObservacionRevisionUseCase {

    private final EstadoObservacionRevisionQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoObservacionRevisionReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(EstadoObservacionRevisionKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
