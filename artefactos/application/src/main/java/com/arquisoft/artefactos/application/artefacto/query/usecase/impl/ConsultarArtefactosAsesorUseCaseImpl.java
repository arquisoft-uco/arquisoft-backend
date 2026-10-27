package com.arquisoft.artefactos.application.artefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosAsesorUseCaseImpl implements ConsultarArtefactosAsesorUseCase {

    private final ArtefactoQueryOutputPort artefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ArtefactoReadModel> ejecutar(ArtefactoAsesorCriteria entrada) {
        logger.debug(ArtefactoKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = artefactoQueryOutputPort.consultarTodosAsesor(entrada);

        logger.debug(ArtefactoKey.LOG_CONSULTA_ASESOR_COMPLETADA, resultado.getTotalElements());
        return resultado;
    }
}
