package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.ConsultarEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasAsesorUseCaseImpl
        implements ConsultarEvaluacionesCualitativasAsesorUseCase {

    private final AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;
    private final ConsultarEvaluacionesCualitativasAsesorValidator consultarEvaluacionesCualitativasAsesorValidator;
    private final EvaluacionCualitativaAsesorQueryOutputPort evaluacionCualitativaAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCualitativaAsesorReadModel> ejecutar(EvaluacionCualitativaAsesorCriteria criteria) {
        logger.debug(EvaluacionCualitativaAsesorKey.LOG_CONSULTANDO, criteria.evaluacionAsesor());

        var asesorAsignado = asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());

        consultarEvaluacionesCualitativasAsesorValidator.validar(
                criteria.evaluacionAsesor(), asesorAsignado, criteria.asesor());

        var resultado = evaluacionCualitativaAsesorQueryOutputPort.consultar(criteria);

        logger.debug(EvaluacionCualitativaAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
