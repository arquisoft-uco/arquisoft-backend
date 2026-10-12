package com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.EvaluacionCuantitativaJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.secondaryport.ObservacionItemJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase.ConsultarObservacionesItemJuradoUseCase;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator.ConsultarObservacionesItemJuradoValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemJuradoUseCaseImpl implements ConsultarObservacionesItemJuradoUseCase {

    private final EvaluacionCuantitativaJuradoExisteQueryFinder evaluacionCuantitativaJuradoExisteQueryFinder;
    private final ConsultarObservacionesItemJuradoValidator consultarObservacionesItemJuradoValidator;
    private final ObservacionItemJuradoQueryOutputPort observacionItemJuradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ObservacionItemJuradoReadModel> ejecutar(ObservacionItemJuradoCriteria criteria) {
        var evaluacionCuantitativaJurado = criteria.getEvaluacionCuantitativaJurado();
        logger.debug(ObservacionItemJuradoKey.LOG_CONSULTANDO, evaluacionCuantitativaJurado,
                criteria.getPagina(), criteria.getTamanio(), criteria.tieneFiltros(), criteria.tieneOrden());

        var existe = evaluacionCuantitativaJuradoExisteQueryFinder.obtener(evaluacionCuantitativaJurado);
        consultarObservacionesItemJuradoValidator.validar(evaluacionCuantitativaJurado, existe);

        var resultado = observacionItemJuradoQueryOutputPort.consultarTodas(criteria);

        logger.debug(ObservacionItemJuradoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
