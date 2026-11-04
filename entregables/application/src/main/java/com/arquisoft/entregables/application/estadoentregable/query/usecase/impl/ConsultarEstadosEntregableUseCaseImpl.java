package com.arquisoft.entregables.application.estadoentregable.query.usecase.impl;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.application.estadoentregable.query.secondaryport.EstadoEntregableQueryOutputPort;
import com.arquisoft.entregables.application.estadoentregable.query.usecase.ConsultarEstadosEntregableUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEntregableUseCaseImpl implements ConsultarEstadosEntregableUseCase {

    private final EstadoEntregableQueryOutputPort estadoEntregableQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoEntregableReadModel> ejecutar() {
        var resultado = estadoEntregableQueryOutputPort.findAll();

        logger.debug(EstadoEntregableKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
