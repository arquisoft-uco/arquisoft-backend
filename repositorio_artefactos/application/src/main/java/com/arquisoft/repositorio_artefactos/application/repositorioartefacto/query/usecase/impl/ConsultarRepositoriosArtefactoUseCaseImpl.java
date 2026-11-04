package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.secondaryport.RepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.usecase.ConsultarRepositoriosArtefactoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRepositoriosArtefactoUseCaseImpl implements ConsultarRepositoriosArtefactoUseCase {

    private final RepositorioArtefactoQueryOutputPort repositorioArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RepositorioArtefactoReadModel> ejecutar(RepositorioArtefactoCriteria entrada) {
        logger.debug(RepositorioArtefactoKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = repositorioArtefactoQueryOutputPort.consultarTodos(entrada);

        logger.debug(RepositorioArtefactoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
