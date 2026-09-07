package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport.EvaluacionCualitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasJuradoUseCaseImpl
        implements ConsultarEvaluacionesCualitativasJuradoUseCase {

    private final EvaluacionJuradoExisteQueryFinder evaluacionJuradoExisteQueryFinder;
    private final EvaluacionJuradoPerteneceEstudianteQueryFinder evaluacionJuradoPerteneceEstudianteQueryFinder;
    private final ConsultarEvaluacionesCualitativasJuradoValidator consultarEvaluacionesCualitativasJuradoValidator;
    private final EvaluacionCualitativaJuradoQueryOutputPort evaluacionCualitativaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCualitativaJuradoReadModel> ejecutar(EvaluacionCualitativaJuradoCriteria criteria) {
        logger.debug(EvaluacionCualitativaJuradoKey.LOG_CONSULTANDO,
                criteria.evaluacionJuradoId(), criteria.estudianteId());

        boolean existe = evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId());
        boolean pertenece = evaluacionJuradoPerteneceEstudianteQueryFinder.obtener(criteria);

        consultarEvaluacionesCualitativasJuradoValidator.validar(criteria.evaluacionJuradoId(), existe, pertenece);

        var resultado = evaluacionCualitativaJuradoQueryOutputPort.consultar(criteria);

        logger.debug(EvaluacionCualitativaJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
