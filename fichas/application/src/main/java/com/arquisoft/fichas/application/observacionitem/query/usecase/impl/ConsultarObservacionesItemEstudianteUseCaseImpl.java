package com.arquisoft.fichas.application.observacionitem.query.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.secondaryport.ObservacionItemQueryOutputPort;
import com.arquisoft.fichas.application.observacionitem.query.usecase.ConsultarObservacionesItemEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemEstudianteUseCaseImpl implements ConsultarObservacionesItemEstudianteUseCase {

    private final ObservacionItemQueryOutputPort observacionItemQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ObservacionItemReadModel> ejecutar(ObservacionItemEstudianteCriteria entrada) {
        logger.debug(ObservacionItemKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = observacionItemQueryOutputPort.consultarTodasEstudiante(entrada);

        logger.debug(ObservacionItemKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
