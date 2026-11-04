package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport.EvaluacionCualitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoRealizadasValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.JuradoDeEvaluacionJuradoQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasJuradoRealizadasUseCaseImpl
        implements ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase {

    private final JuradoDeEvaluacionJuradoQueryFinder juradoDeEvaluacionJuradoQueryFinder;
    private final ConsultarEvaluacionesCualitativasJuradoRealizadasValidator validator;
    private final EvaluacionCualitativaJuradoQueryOutputPort evaluacionCualitativaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCualitativaJuradoReadModel> ejecutar(EvaluacionCualitativaJuradoRealizadaCriteria criteria) {
        logger.debug(EvaluacionCualitativaJuradoKey.LOG_CONSULTANDO_REALIZADAS,
                criteria.evaluacionJurado(), criteria.jurado());

        var juradoAsignado = juradoDeEvaluacionJuradoQueryFinder.obtener(criteria.evaluacionJurado());
        var existe = !UtilUUID.esPorDefecto(juradoAsignado);

        validator.validar(criteria.evaluacionJurado(), existe, juradoAsignado, criteria.jurado());

        var resultado = evaluacionCualitativaJuradoQueryOutputPort.consultar(
                new EvaluacionCualitativaJuradoCriteria(criteria.evaluacionJurado()));

        logger.debug(EvaluacionCualitativaJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
