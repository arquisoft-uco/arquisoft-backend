package com.arquisoft.mapas_ruta.application.estadoitem.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.application.estadoitem.query.secondaryport.EstadoItemQueryOutputPort;
import com.arquisoft.mapas_ruta.application.estadoitem.query.usecase.ConsultarEstadosItemUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.EstadoItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosItemUseCaseImpl implements ConsultarEstadosItemUseCase {

    private final EstadoItemQueryOutputPort estadoItemQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoItemReadModel> ejecutar() {
        var resultado = estadoItemQueryOutputPort.findAll();

        logger.debug(EstadoItemKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
