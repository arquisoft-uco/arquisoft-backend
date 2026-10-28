package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.usecase.ConsultarVersionesArtefactoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesArtefactoAsesorUseCaseImpl implements ConsultarVersionesArtefactoAsesorUseCase {

    private final VersionArtefactoQueryOutputPort versionArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<VersionArtefactoReadModel> ejecutar(VersionArtefactoAsesorCriteria entrada) {
        logger.debug(VersionArtefactoKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = versionArtefactoQueryOutputPort.consultarTodasAsesor(entrada);

        logger.debug(VersionArtefactoKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements());
        return resultado;
    }
}
