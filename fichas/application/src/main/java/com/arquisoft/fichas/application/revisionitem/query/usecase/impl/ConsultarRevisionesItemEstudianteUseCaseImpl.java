package com.arquisoft.fichas.application.revisionitem.query.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.secondaryport.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.application.revisionitem.query.usecase.ConsultarRevisionesItemEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesItemEstudianteUseCaseImpl implements ConsultarRevisionesItemEstudianteUseCase {

    private final RevisionItemQueryOutputPort revisionItemQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RevisionItemReadModel> ejecutar(RevisionItemEstudianteCriteria entrada) {
        logger.debug(RevisionItemKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = revisionItemQueryOutputPort.consultarTodasEstudiante(entrada);

        logger.debug(RevisionItemKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
