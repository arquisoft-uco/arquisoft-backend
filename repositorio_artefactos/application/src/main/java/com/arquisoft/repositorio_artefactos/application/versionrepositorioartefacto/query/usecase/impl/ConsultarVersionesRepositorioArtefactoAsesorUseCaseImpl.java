package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesRepositorioArtefactoAsesorUseCaseImpl
        implements ConsultarVersionesRepositorioArtefactoAsesorUseCase {

    private final VersionRepositorioArtefactoQueryOutputPort versionRepositorioArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<VersionRepositorioArtefactoAsesorReadModel> ejecutar(
            VersionRepositorioArtefactoAsesorCriteria entrada) {
        logger.debug(VersionRepositorioArtefactoKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(), entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = versionRepositorioArtefactoQueryOutputPort.consultarParaAsesor(entrada);

        logger.debug(VersionRepositorioArtefactoKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements(), resultado.getPage(), resultado.getSize());
        return resultado;
    }
}
