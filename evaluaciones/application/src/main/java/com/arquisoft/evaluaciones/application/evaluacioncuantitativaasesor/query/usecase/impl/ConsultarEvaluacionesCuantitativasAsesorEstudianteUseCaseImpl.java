package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EstudiantesDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator.ConsultarEvaluacionesCuantitativasAsesorEstudianteValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCaseImpl
        implements ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase {

    private final AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;
    private final EstudiantesDeEvaluacionAsesorQueryFinder estudiantesDeEvaluacionAsesorQueryFinder;
    private final ConsultarEvaluacionesCuantitativasAsesorEstudianteValidator
            consultarEvaluacionesCuantitativasAsesorEstudianteValidator;
    private final EvaluacionCuantitativaAsesorQueryOutputPort evaluacionCuantitativaAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionCuantitativaAsesorReadModel> ejecutar(
            EvaluacionCuantitativaAsesorEstudianteCriteria criteria) {
        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_CONSULTANDO, criteria.evaluacionAsesor());

        var asesorAsignado = asesorDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());
        var estudiantesVinculados = estudiantesDeEvaluacionAsesorQueryFinder.obtener(criteria.evaluacionAsesor());

        consultarEvaluacionesCuantitativasAsesorEstudianteValidator.validar(
                criteria.evaluacionAsesor(), asesorAsignado, estudiantesVinculados, criteria.estudiante());

        var resultado = evaluacionCuantitativaAsesorQueryOutputPort.consultarPorEvaluacionAsesor(
                criteria.evaluacionAsesor());

        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
