package com.arquisoft.mapas_ruta.application.planestimado.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.secondaryport.PlanEstimadoQueryOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosTareaEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesEstimadosTareaEstudianteUseCaseImpl
        implements ConsultarPlanesEstimadosTareaEstudianteUseCase {

    private final PlanEstimadoQueryOutputPort planEstimadoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<PlanEstimadoReadModel> ejecutar(PlanEstimadoTareaCriteria entrada) {
        logger.debug(PlanEstimadoKey.LOG_CONSULTANDO,
                entrada.getTarea(), entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = planEstimadoQueryOutputPort.consultarPorTarea(entrada);

        logger.debug(PlanEstimadoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
