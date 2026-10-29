package com.arquisoft.proyectos.application.estadoasesor.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosPosiblesAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosPosiblesAsesorUseCaseImpl implements ConsultarEstadosPosiblesAsesorUseCase {

    private final EstadoAsesorQueryOutputPort estadoAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EstadoAsesorReadModel> ejecutar(EstadoAsesorCriteria entrada) {
        logger.debug(EstadoAsesorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = estadoAsesorQueryOutputPort.consultarTodos(entrada);

        logger.debug(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
