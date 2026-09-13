package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.ConsultarEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasJuradoUseCaseImpl
        implements ConsultarEvaluacionesCuantitativasJuradoUseCase {

    private final EvaluacionJuradoExisteQueryFinder evaluacionJuradoExisteQueryFinder;
    private final EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder evaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder;
    private final ConsultarEvaluacionesCuantitativasJuradoValidator consultarEvaluacionesCuantitativasJuradoValidator;
    private final EvaluacionCuantitativaJuradoQueryOutputPort evaluacionCuantitativaJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCuantitativaJuradoReadModel> ejecutar(EvaluacionCuantitativaJuradoCriteria criteria) {
        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_CONSULTANDO,
                criteria.evaluacionJuradoId(), criteria.estudianteId());

        boolean existe = evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId());
        boolean pertenece = evaluacionJuradoPerteneceEstudianteCuantitativaQueryFinder.obtener(criteria);

        consultarEvaluacionesCuantitativasJuradoValidator.validar(criteria.evaluacionJuradoId(), existe, pertenece);

        var resultado = evaluacionCuantitativaJuradoQueryOutputPort.consultar(criteria);

        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
