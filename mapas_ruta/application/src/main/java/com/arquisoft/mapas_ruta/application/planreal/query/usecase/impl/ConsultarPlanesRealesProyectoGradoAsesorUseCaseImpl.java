package com.arquisoft.mapas_ruta.application.planreal.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.secondaryport.PlanRealQueryOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesProyectoGradoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesRealesProyectoGradoAsesorUseCaseImpl
        implements ConsultarPlanesRealesProyectoGradoAsesorUseCase {

    private final PlanRealQueryOutputPort planRealQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<PlanRealProyectoReadModel> ejecutar(PlanRealProyectoCriteria entrada) {
        logger.debug(PlanRealKey.LOG_CONSULTANDO_POR_PROYECTO_GRADO,
                entrada.getProyectoGrado(), entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = planRealQueryOutputPort.consultarPorProyectoGrado(entrada);

        logger.debug(PlanRealKey.LOG_CONSULTA_POR_PROYECTO_GRADO_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
