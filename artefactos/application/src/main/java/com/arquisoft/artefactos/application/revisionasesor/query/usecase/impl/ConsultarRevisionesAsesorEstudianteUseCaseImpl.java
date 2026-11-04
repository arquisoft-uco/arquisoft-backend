package com.arquisoft.artefactos.application.revisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.secondaryport.RevisionAsesorQueryOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.query.usecase.ConsultarRevisionesAsesorEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRevisionesAsesorEstudianteUseCaseImpl implements ConsultarRevisionesAsesorEstudianteUseCase {

    private final RevisionAsesorQueryOutputPort revisionAsesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RevisionAsesorReadModel> ejecutar(RevisionAsesorEstudianteCriteria entrada) {
        logger.debug(RevisionAsesorKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = revisionAsesorQueryOutputPort.consultarTodasEstudiante(entrada);

        logger.debug(RevisionAsesorKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
