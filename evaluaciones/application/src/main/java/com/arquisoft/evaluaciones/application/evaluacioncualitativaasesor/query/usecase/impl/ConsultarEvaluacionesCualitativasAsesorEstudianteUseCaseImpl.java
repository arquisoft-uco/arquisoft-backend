package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EstudiantesDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorEstudianteQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.ConsultarEvaluacionesCualitativasAsesorEstudianteValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasAsesorEstudianteUseCaseImpl
        implements ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase {

    private final AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;
    private final EstudiantesDeEvaluacionAsesorQueryFinder estudiantesDeEvaluacionAsesorQueryFinder;
    private final ConsultarEvaluacionesCualitativasAsesorEstudianteValidator
            consultarEvaluacionesCualitativasAsesorEstudianteValidator;
    private final EvaluacionCualitativaAsesorEstudianteQueryOutputPort
            evaluacionCualitativaAsesorEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCualitativaAsesorEstudianteReadModel> ejecutar(
            EvaluacionCualitativaAsesorEstudianteCriteria criteria) {
        logger.debug(EvaluacionCualitativaAsesorKey.LOG_CONSULTANDO, criteria.evaluacionAsesor());

        var asesorAsignado = asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());
        var estudiantesVinculados = estudiantesDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());

        consultarEvaluacionesCualitativasAsesorEstudianteValidator.validar(
                criteria.evaluacionAsesor(), asesorAsignado, estudiantesVinculados, criteria.estudiante());

        var resultado = evaluacionCualitativaAsesorEstudianteQueryOutputPort.consultarPorEvaluacionAsesor(
                criteria.evaluacionAsesor());

        logger.debug(EvaluacionCualitativaAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
