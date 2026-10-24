package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.usecase.ConsultarVersionesRepositorioArtefactoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarVersionesRepositorioArtefactoUseCaseImpl
        implements ConsultarVersionesRepositorioArtefactoUseCase {

    private final VersionRepositorioArtefactoQueryOutputPort versionRepositorioArtefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<VersionRepositorioArtefactoReadModel> ejecutar(VersionRepositorioArtefactoCriteria entrada) {
        logger.debug(VersionRepositorioArtefactoKey.LOG_CONSULTANDO, entrada.repositorioArtefacto());

        var resultado = versionRepositorioArtefactoQueryOutputPort
                .consultarPorRepositorio(entrada.repositorioArtefacto());

        logger.debug(VersionRepositorioArtefactoKey.LOG_CONSULTA_COMPLETADA, resultado.size());
        return resultado;
    }
}
