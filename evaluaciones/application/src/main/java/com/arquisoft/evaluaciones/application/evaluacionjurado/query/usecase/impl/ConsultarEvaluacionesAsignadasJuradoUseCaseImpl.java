package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesAsignadasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesAsignadasJuradoValidator;
import com.arquisoft.evaluaciones.application.jurado.query.finder.JuradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsignadasJuradoUseCaseImpl implements ConsultarEvaluacionesAsignadasJuradoUseCase {

    private final JuradoExisteQueryFinder juradoExisteQueryFinder;
    private final ConsultarEvaluacionesAsignadasJuradoValidator consultarEvaluacionesAsignadasJuradoValidator;
    private final EvaluacionAsignadaJuradoQueryOutputPort evaluacionAsignadaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionAsignadaJuradoReadModel> ejecutar(EvaluacionesAsignadasJuradoCriteria criteria) {
        logger.debug(EvaluacionJuradoKey.LOG_CONSULTANDO_ASIGNADAS, criteria.jurado());

        var existe = juradoExisteQueryFinder.obtener(criteria.jurado());
        consultarEvaluacionesAsignadasJuradoValidator.validar(criteria.jurado(), existe);

        var resultado = evaluacionAsignadaJuradoQueryOutputPort.consultar(criteria);

        logger.debug(EvaluacionJuradoKey.LOG_CONSULTA_ASIGNADAS_COMPLETADA, resultado.size());
        return resultado;
    }
}
