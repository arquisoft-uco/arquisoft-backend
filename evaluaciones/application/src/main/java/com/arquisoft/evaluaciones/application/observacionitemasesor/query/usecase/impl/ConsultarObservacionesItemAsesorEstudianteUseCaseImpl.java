package com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.AccesoEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.secondaryport.ObservacionItemAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.ConsultarObservacionesItemAsesorEstudianteUseCase;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.ConsultarObservacionesItemAsesorEstudianteValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemAsesorEstudianteUseCaseImpl
        implements ConsultarObservacionesItemAsesorEstudianteUseCase {

    private final AccesoEvaluacionCuantitativaAsesorQueryFinder accesoEvaluacionCuantitativaAsesorQueryFinder;
    private final EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder estudiantesDeEvaluacionCuantitativaAsesorQueryFinder;
    private final ConsultarObservacionesItemAsesorEstudianteValidator consultarObservacionesItemAsesorEstudianteValidator;
    private final ObservacionItemAsesorQueryOutputPort observacionItemAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ObservacionItemAsesorReadModel> ejecutar(ObservacionItemAsesorCriteria criteria) {
        var evaluacionCuantitativaAsesor = criteria.getEvaluacionCuantitativaAsesor();
        logger.debug(ObservacionItemAsesorKey.LOG_CONSULTANDO, evaluacionCuantitativaAsesor,
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var acceso = accesoEvaluacionCuantitativaAsesorQueryFinder.obtener(evaluacionCuantitativaAsesor);
        var estudiantesVinculados = estudiantesDeEvaluacionCuantitativaAsesorQueryFinder
                .obtener(evaluacionCuantitativaAsesor);
        consultarObservacionesItemAsesorEstudianteValidator.validar(
                evaluacionCuantitativaAsesor, acceso, estudiantesVinculados, criteria.getEstudiante());

        var resultado = observacionItemAsesorQueryOutputPort.consultarTodas(criteria);

        logger.debug(ObservacionItemAsesorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
