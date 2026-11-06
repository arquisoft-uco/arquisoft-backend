package com.arquisoft.mapas_ruta.application.planreal.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.secondaryport.PlanRealQueryOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesTareaEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesRealesTareaEstudianteUseCaseImpl
        implements ConsultarPlanesRealesTareaEstudianteUseCase {

    private final PlanRealQueryOutputPort planRealQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<PlanRealReadModel> ejecutar(PlanRealTareaCriteria entrada) {
        logger.debug(PlanRealKey.LOG_CONSULTANDO,
                entrada.getTarea(), entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = planRealQueryOutputPort.consultarPorTarea(entrada);

        logger.debug(PlanRealKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
