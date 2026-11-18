package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.ConsultarEvaluacionesCuantitativasJuradoRealizadasValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.JuradoDeEvaluacionJuradoQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCaseImpl
        implements ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase {

    private final JuradoDeEvaluacionJuradoQueryFinder juradoDeEvaluacionJuradoQueryFinder;
    private final ConsultarEvaluacionesCuantitativasJuradoRealizadasValidator validator;
    private final EvaluacionCuantitativaJuradoQueryOutputPort evaluacionCuantitativaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCuantitativaJuradoReadModel> ejecutar(EvaluacionCuantitativaJuradoRealizadaCriteria criteria) {
        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_CONSULTANDO_REALIZADAS,
                criteria.evaluacionJurado(), criteria.jurado());

        var juradoAsignado = juradoDeEvaluacionJuradoQueryFinder.obtener(criteria.evaluacionJurado());
        var existe = !UtilUUID.esPorDefecto(juradoAsignado);

        validator.validar(criteria.evaluacionJurado(), existe, juradoAsignado, criteria.jurado());

        var resultado = evaluacionCuantitativaJuradoQueryOutputPort.consultar(
                new EvaluacionCuantitativaJuradoCriteria(criteria.evaluacionJurado()));

        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
