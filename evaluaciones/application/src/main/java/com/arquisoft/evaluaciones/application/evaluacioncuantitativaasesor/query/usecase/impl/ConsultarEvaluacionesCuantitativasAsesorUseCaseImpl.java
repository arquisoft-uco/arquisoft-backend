package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator.ConsultarEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasAsesorUseCaseImpl
        implements ConsultarEvaluacionesCuantitativasAsesorUseCase {

    private final AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;
    private final ConsultarEvaluacionesCuantitativasAsesorValidator consultarEvaluacionesCuantitativasAsesorValidator;
    private final EvaluacionCuantitativaAsesorQueryOutputPort evaluacionCuantitativaAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCuantitativaAsesorReadModel> ejecutar(EvaluacionCuantitativaAsesorCriteria criteria) {
        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_CONSULTANDO, criteria.evaluacionAsesor());

        var asesorAsignado = asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());

        consultarEvaluacionesCuantitativasAsesorValidator.validar(
                criteria.evaluacionAsesor(), asesorAsignado, criteria.asesor());

        var resultado = evaluacionCuantitativaAsesorQueryOutputPort.consultar(criteria);

        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
