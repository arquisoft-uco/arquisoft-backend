package com.arquisoft.fichas.application.revisionitem.query.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.secondaryport.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.application.revisionitem.query.usecase.ConsultarRevisionesItemAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesItemAsesorUseCaseImpl implements ConsultarRevisionesItemAsesorUseCase {

    private final RevisionItemQueryOutputPort revisionItemQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RevisionItemReadModel> ejecutar(RevisionItemCriteria entrada) {
        logger.debug(RevisionItemKey.LOG_CONSULTANDO_ELABORADAS,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = revisionItemQueryOutputPort.consultarTodas(entrada);

        logger.debug(RevisionItemKey.LOG_CONSULTA_ELABORADAS_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
