package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoCoordinadorUseCaseImpl implements ConsultarProyectosGradoCoordinadorUseCase {

    private final ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ProyectoGradoReadModel> ejecutar(ProyectoGradoCriteria entrada) {
        logger.debug(ProyectoGradoKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = proyectoGradoQueryOutputPort.consultarTodas(entrada);

        logger.debug(ProyectoGradoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
