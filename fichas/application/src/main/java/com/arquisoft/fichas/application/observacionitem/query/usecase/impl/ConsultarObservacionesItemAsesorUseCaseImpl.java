package com.arquisoft.fichas.application.observacionitem.query.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.secondaryport.ObservacionItemQueryOutputPort;
import com.arquisoft.fichas.application.observacionitem.query.usecase.ConsultarObservacionesItemAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesItemAsesorUseCaseImpl implements ConsultarObservacionesItemAsesorUseCase {

    private final ObservacionItemQueryOutputPort observacionItemQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ObservacionItemReadModel> ejecutar(ObservacionItemCriteria entrada) {
        logger.debug(ObservacionItemKey.LOG_CONSULTANDO_ELABORADAS,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = observacionItemQueryOutputPort.consultarTodas(entrada);

        logger.debug(ObservacionItemKey.LOG_CONSULTA_ELABORADAS_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
