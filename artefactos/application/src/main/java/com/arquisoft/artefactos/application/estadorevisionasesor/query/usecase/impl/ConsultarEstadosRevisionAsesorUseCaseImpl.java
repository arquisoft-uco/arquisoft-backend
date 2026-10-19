package com.arquisoft.artefactos.application.estadorevisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.secondaryport.EstadoRevisionAsesorQueryOutputPort;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.usecase.ConsultarEstadosRevisionAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstadoRevisionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRevisionAsesorUseCaseImpl implements ConsultarEstadosRevisionAsesorUseCase {

    private final EstadoRevisionAsesorQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoRevisionAsesorReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(EstadoRevisionAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
