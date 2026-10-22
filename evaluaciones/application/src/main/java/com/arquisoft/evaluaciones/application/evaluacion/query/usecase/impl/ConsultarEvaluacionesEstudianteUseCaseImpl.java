package com.arquisoft.evaluaciones.application.evaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionEstudianteQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesEstudianteUseCaseImpl implements ConsultarEvaluacionesEstudianteUseCase {

    private final EvaluacionEstudianteQueryOutputPort evaluacionEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionReadModel> ejecutar(EvaluacionEstudianteCriteria criteria) {
        logger.debug(EvaluacionKey.LOG_CONSULTANDO,
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var resultado = evaluacionEstudianteQueryOutputPort.consultarPorEstudiante(criteria);

        logger.debug(EvaluacionKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
