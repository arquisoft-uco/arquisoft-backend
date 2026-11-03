package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoAccesoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.entity.ArchivoVersionEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper.ArchivoVersionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoAccesoQueryOutputAdapter
        implements VersionRepositorioArtefactoAccesoQueryOutputPort {

    private final ArchivoVersionQueryRepository archivoVersionQueryRepository;

    @Override
    public Optional<ArchivoVersionEntity> obtenerArchivoPorId(UUID version) {
        return archivoVersionQueryRepository.findById(version).map(ArchivoVersionQueryMapper::toEntity);
    }
}
