package com.arquisoft.mapas_ruta.application.planestimado.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.secondaryport.PlanEstimadoQueryOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosProyectoGradoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesEstimadosProyectoGradoAsesorUseCaseImpl
        implements ConsultarPlanesEstimadosProyectoGradoAsesorUseCase {

    private final PlanEstimadoQueryOutputPort planEstimadoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<PlanEstimadoProyectoReadModel> ejecutar(PlanEstimadoProyectoCriteria entrada) {
        logger.debug(PlanEstimadoKey.LOG_CONSULTANDO_POR_PROYECTO_GRADO,
                entrada.getProyectoGrado(), entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = planEstimadoQueryOutputPort.consultarPorProyectoGrado(entrada);

        logger.debug(PlanEstimadoKey.LOG_CONSULTA_POR_PROYECTO_GRADO_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
