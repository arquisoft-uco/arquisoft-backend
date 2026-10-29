package com.arquisoft.proyectos.application.estadoasesor.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosPosiblesAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosPosiblesAsesorUseCaseImpl implements ConsultarEstadosPosiblesAsesorUseCase {

    private final EstadoAsesorQueryOutputPort estadoAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoAsesorReadModel> ejecutar() {
        var resultado = estadoAsesorQueryOutputPort.consultarTodos();

        logger.debug(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());
        return resultado;
    }
}
