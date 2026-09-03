package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaUseCaseImpl implements ConsultarEstadosFichaUseCase {

    private final EstadoFichaQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoFichaReadModel> ejecutar() {
        var resultado = queryOutputPort.findAll();

        logger.debug(EstadoFichaKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
