package com.arquisoft.artefactos.application.revisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.secondaryport.RevisionAsesorQueryOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.query.usecase.ConsultarRevisionesAsesorAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesAsesorAsesorUseCaseImpl implements ConsultarRevisionesAsesorAsesorUseCase {

    private final RevisionAsesorQueryOutputPort revisionAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RevisionAsesorReadModel> ejecutar(RevisionAsesorAsesorCriteria entrada) {
        logger.debug(RevisionAsesorKey.LOG_CONSULTANDO_ASESOR,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = revisionAsesorQueryOutputPort.consultarTodasAsesor(entrada);

        logger.debug(RevisionAsesorKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
