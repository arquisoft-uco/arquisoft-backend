package com.arquisoft.evaluaciones.application.evaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCoordinadorUseCaseImpl implements ConsultarEvaluacionesCoordinadorUseCase {

    private final EvaluacionQueryOutputPort evaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionReadModel> ejecutar(EvaluacionCriteria criteria) {
        logger.debug(EvaluacionKey.LOG_CONSULTANDO,
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var resultado = evaluacionQueryOutputPort.consultarTodas(criteria);

        logger.debug(EvaluacionKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
