package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.finder.EvaluacionExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.EvaluacionAsesorDeEvaluacionQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorDeEvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionAsesorUseCaseImpl implements ConsultarEvaluacionAsesorUseCase {

    private final EvaluacionExisteQueryFinder evaluacionExisteQueryFinder;
    private final EvaluacionAsesorDeEvaluacionQueryFinder evaluacionAsesorDeEvaluacionQueryFinder;
    private final ConsultarEvaluacionAsesorValidator consultarEvaluacionAsesorValidator;
    private final EvaluacionAsesorDeEvaluacionQueryOutputPort evaluacionAsesorDeEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EvaluacionAsesorReadModel ejecutar(EvaluacionAsesorDeEvaluacionCriteria entrada) {
        logger.debug(EvaluacionAsesorKey.LOG_CONSULTANDO, entrada.evaluacion());

        var evaluacionExiste = evaluacionExisteQueryFinder.obtener(entrada.evaluacion());
        var evaluacionAsesor = evaluacionAsesorDeEvaluacionQueryFinder.obtener(entrada.evaluacion());

        consultarEvaluacionAsesorValidator.validar(entrada.evaluacion(), evaluacionExiste, evaluacionAsesor);

        var resultado = evaluacionAsesorDeEvaluacionQueryOutputPort.consultar(entrada);

        logger.debug(EvaluacionAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.id());
        return resultado;
    }
}
