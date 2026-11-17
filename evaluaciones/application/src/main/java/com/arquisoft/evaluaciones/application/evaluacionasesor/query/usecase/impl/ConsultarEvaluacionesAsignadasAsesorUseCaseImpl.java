package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsignadaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionesAsignadasAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsignadasAsesorUseCaseImpl implements ConsultarEvaluacionesAsignadasAsesorUseCase {

    private final EvaluacionAsignadaAsesorQueryOutputPort evaluacionAsignadaAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionAsignadaAsesorReadModel> ejecutar(EvaluacionesAsignadasAsesorCriteria criteria) {
        logger.debug(EvaluacionAsesorKey.LOG_CONSULTANDO_ASIGNADAS, criteria.getAsesor(),
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var resultado = evaluacionAsignadaAsesorQueryOutputPort.consultarTodas(criteria);

        logger.debug(EvaluacionAsesorKey.LOG_CONSULTA_ASIGNADAS_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
