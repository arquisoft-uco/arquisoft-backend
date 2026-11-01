package com.arquisoft.artefactos.application.artefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.artefactos.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.artefacto.query.usecase.ConsultarArtefactosSinAvanceUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarArtefactosSinAvanceUseCaseImpl implements ConsultarArtefactosSinAvanceUseCase {

    private final ArtefactoQueryOutputPort artefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<ArtefactoSinAvanceReadModel> ejecutar(ArtefactoSinAvanceCriteria entrada) {
        logger.debug(ArtefactoKey.LOG_CONSULTANDO_SIN_AVANCE,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = artefactoQueryOutputPort.consultarSinAvance(entrada);

        logger.debug(ArtefactoKey.LOG_CONSULTA_SIN_AVANCE_COMPLETADA, resultado.getTotalElements());
        return resultado;
    }
}
