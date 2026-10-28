package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesAsignadasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesAsignadasJuradoValidator;
import com.arquisoft.evaluaciones.application.jurado.query.finder.JuradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsignadasJuradoUseCaseImpl implements ConsultarEvaluacionesAsignadasJuradoUseCase {

    private final JuradoExisteQueryFinder juradoExisteQueryFinder;
    private final ConsultarEvaluacionesAsignadasJuradoValidator consultarEvaluacionesAsignadasJuradoValidator;
    private final EvaluacionAsignadaJuradoQueryOutputPort evaluacionAsignadaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionAsignadaJuradoReadModel> ejecutar(EvaluacionesAsignadasJuradoCriteria criteria) {
        logger.debug(EvaluacionJuradoKey.LOG_CONSULTANDO_ASIGNADAS, criteria.getJurado(),
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var existe = juradoExisteQueryFinder.obtener(criteria.getJurado());
        consultarEvaluacionesAsignadasJuradoValidator.validar(criteria.getJurado(), existe);

        var resultado = evaluacionAsignadaJuradoQueryOutputPort.consultarTodas(criteria);

        logger.debug(EvaluacionJuradoKey.LOG_CONSULTA_ASIGNADAS_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
