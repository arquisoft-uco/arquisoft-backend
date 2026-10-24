package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper.VersionRepositorioArtefactoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoQueryOutputAdapter implements VersionRepositorioArtefactoQueryOutputPort {

    private final VersionRepositorioArtefactoQueryRepository versionRepositorioArtefactoQueryRepository;

    @Override
    public List<VersionRepositorioArtefactoReadModel> consultarPorRepositorio(UUID repositorioArtefacto) {
        return versionRepositorioArtefactoQueryRepository
                .findByRepositorioArtefactoOrderByNumeroVersionDescNombreDocumentoAsc(repositorioArtefacto)
                .stream()
                .map(VersionRepositorioArtefactoQueryMapper::toReadModel)
                .toList();
    }
}
