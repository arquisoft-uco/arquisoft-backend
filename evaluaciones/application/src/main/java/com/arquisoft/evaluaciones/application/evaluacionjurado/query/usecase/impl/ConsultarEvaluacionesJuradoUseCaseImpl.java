package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.finder.EvaluacionExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesJuradoValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesJuradoUseCaseImpl implements ConsultarEvaluacionesJuradoUseCase {

    private final EvaluacionExisteQueryFinder evaluacionExisteQueryFinder;
    private final ConsultarEvaluacionesJuradoValidator consultarEvaluacionesJuradoValidator;
    private final EvaluacionJuradoQueryOutputPort evaluacionJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionJuradoReadModel> ejecutar(EvaluacionJuradoCriteria criteria) {
        logger.debug(EvaluacionJuradoKey.LOG_CONSULTANDO, criteria.getEvaluacion(),
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var existe = evaluacionExisteQueryFinder.obtener(criteria.getEvaluacion());
        consultarEvaluacionesJuradoValidator.validar(criteria.getEvaluacion(), existe);

        var resultado = evaluacionJuradoQueryOutputPort.consultarTodas(criteria);

        logger.debug(EvaluacionJuradoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
